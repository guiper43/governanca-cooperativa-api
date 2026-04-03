package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.RegistroParticipacao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.UrnaVoto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.RegistroParticipacaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.UrnaVotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.service.gateway.CpfValidatorGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotoService {
    private final RegistroParticipacaoRepository registroParticipacaoRepository;
    private final UrnaVotoRepository urnaVotoRepository;
    private final SessaoRepository sessaoRepository;
    private final PautaService pautaService;
    private final CpfValidatorGateway cpfValidatorGateway;

    public VotoOutput votar(UUID pautaId, VotoInput request) {
        log.info("Iniciando voto. pautaId={}", pautaId);

        Sessao sessao = buscarSessaoAberta(pautaId);
        validarHorarioSessao(sessao, pautaId);
        validarAssociado(request.associadoId(), pautaId, sessao.getId());

        Pauta pauta = pautaService.buscarEntidade(pautaId);
        UUID votoId = registrarParticipacaoEDepositarCedula(pauta, sessao, request);

        log.info("Voto registrado com sucesso. votoId={} pautaId={} sessaoId={}",
                votoId, pautaId, sessao.getId());
        return new VotoOutput(votoId, pauta.getId(), "REGISTRADO");
    }

    private Sessao buscarSessaoAberta(UUID pautaId) {
        return sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> {
                    log.warn("Sessao nao encontrada para pauta. pautaId={}", pautaId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessao nao encontrada para a pauta");
                });
    }

    private void validarHorarioSessao(Sessao sessao, UUID pautaId) {
        if (LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            log.warn("Sessao encerrada. pautaId={} sessaoId={} dataFechamento={}", pautaId, sessao.getId(),
                    sessao.getDataFechamento());
            throw new BusinessException("Sessao encerrada");
        }
    }

    private void validarAssociado(String associadoId, UUID pautaId, UUID sessaoId) {
        CpfValidationStatus statusCpf = cpfValidatorGateway.validar(associadoId);
        if (statusCpf == CpfValidationStatus.UNABLE_TO_VOTE) {
            log.warn("Associado inapto a votar. pautaId={} sessaoId={}", pautaId, sessaoId);
            throw new BusinessException("CPF nao esta apto a votar");
        }
    }

    private UUID registrarParticipacaoEDepositarCedula(Pauta pauta, Sessao sessao, VotoInput request) {
        if (registroParticipacaoRepository.existsByPautaIdAndAssociadoId(pauta.getId(), request.associadoId())) {
            log.warn("Tentativa de voto duplicado. pautaId={} sessaoId={}", pauta.getId(), sessao.getId());
            throw new BusinessException("Associado ja votou nessa sessao");
        }

        UUID votoId = UUID.randomUUID();
        RegistroParticipacao registroParticipacao = RegistroParticipacao.emitir(UUID.randomUUID(), pauta,
                request.associadoId(), gerarTokenHash());
        UrnaVoto urnaVoto = UrnaVoto.depositar(votoId, pauta, request.votoEscolha());

        try {
            registroParticipacaoRepository.save(registroParticipacao);
            registroParticipacao.consumir();
            registroParticipacaoRepository.save(registroParticipacao);
            urnaVotoRepository.save(urnaVoto);
            return votoId;
        } catch (DataIntegrityViolationException e) {
            log.warn("Tentativa de voto duplicado. pautaId={} sessaoId={}", pauta.getId(), sessao.getId());
            throw new BusinessException("Associado ja votou nessa sessao");
        }
    }

    private String gerarTokenHash() {
        try {
            String token = UUID.randomUUID().toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash nao disponivel", e);
        }
    }
}
