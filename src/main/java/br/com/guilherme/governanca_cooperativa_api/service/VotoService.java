package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Voto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.service.gateway.CpfValidatorGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.CpfUtils.mascararCpf;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotoService {
    private final VotoRepository votoRepository;
    private final SessaoRepository sessaoRepository;
    private final PautaService pautaService;
    private final CpfValidatorGateway cpfValidatorGateway;

    public VotoOutput votar(UUID pautaId, VotoInput request) {
        log.info("Iniciando voto. pautaId={}", pautaId);

        Sessao sessao = buscarSessaoAberta(pautaId);
        validarHorarioSessao(sessao, pautaId);
        validarAssociado(request.associadoId(), pautaId, sessao.getId());

        Pauta pauta = pautaService.buscarEntidade(pautaId);
        Voto voto = criarEPersistirVoto(pauta, sessao, request);

        log.info("Voto registrado com sucesso. votoId={} pautaId={} sessaoId={} escolha={}",
                voto.getId(), pautaId, sessao.getId(), voto.getVotoEscolha());
        return new VotoOutput(voto.getId(), pauta.getId(), mascararCpf(voto.getAssociadoId()), voto.getVotoEscolha());
    }

    private Sessao buscarSessaoAberta(UUID pautaId) {
        return sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> {
                    log.warn("Sessão não encontrada para pauta. pautaId={}", pautaId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada para a pauta");
                });
    }

    private void validarHorarioSessao(Sessao sessao, UUID pautaId) {
        if (LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            log.warn("Sessão encerrada. pautaId={} sessaoId={} dataFechamento={}", pautaId, sessao.getId(),
                    sessao.getDataFechamento());
            throw new BusinessException("Sessão encerrada");
        }
    }

    private void validarAssociado(String associadoId, UUID pautaId, UUID sessaoId) {
        CpfValidationStatus statusCpf = cpfValidatorGateway.validar(associadoId);
        if (statusCpf == CpfValidationStatus.UNABLE_TO_VOTE) {
            log.warn("Associado inapto a votar. pautaId={} sessaoId={} cpf={}", pautaId, sessaoId,
                    mascararCpf(associadoId));
            throw new BusinessException("CPF não está apto a votar");
        }
    }

    private Voto criarEPersistirVoto(Pauta pauta, Sessao sessao, VotoInput request) {
        UUID id = UUID.randomUUID();
        Voto voto = Voto.criar(id, pauta, request.associadoId(), request.votoEscolha());
        try {
            return votoRepository.save(voto);
        } catch (DataIntegrityViolationException e) {
            log.warn("Tentativa de voto duplicado. pautaId={} sessaoId={}", pauta.getId(), sessao.getId());
            throw new BusinessException("Associado já votou nessa sessão");
        }
    }

}