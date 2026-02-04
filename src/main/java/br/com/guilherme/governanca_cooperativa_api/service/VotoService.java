package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.service.gateway.CpfValidatorGateway;

import static br.com.guilherme.governanca_cooperativa_api.utils.CpfUtils.mascararCpf;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Voto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

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
        Sessao sessao = sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> {
                    log.warn("Sessão não encontrada para pauta. pautaId={}", pautaId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada para a pauta");
                });

        if (LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            log.warn("Sessão encerrada. pautaId={} sessaoId={} dataFechamento={}", pautaId, sessao.getId(),
                    sessao.getDataFechamento());
            throw new BusinessException("Sessão encerrada");
        }

        CpfValidationStatus statusCpf = cpfValidatorGateway.validar(request.associadoId());

        if (statusCpf == CpfValidationStatus.UNABLE_TO_VOTE) {
            log.warn("Associado inapto a votar. pautaId={} sessaoId={} cpf={}", pautaId, sessao.getId(),
                    mascararCpf(request.associadoId()));
            throw new BusinessException("CPF não está apto a votar");
        }

        Pauta pauta = pautaService.buscarEntidade(pautaId);
        UUID id = UUID.randomUUID();
        Voto voto = Voto.criar(id, pauta, request.associadoId(), request.votoEscolha());

        try {
            votoRepository.save(voto);
        } catch (DataIntegrityViolationException e) {
            log.warn("Tentativa de voto duplicado. pautaId={} sessaoId={}", pautaId, sessao.getId());
            throw new BusinessException("Associado já votou nessa sessão");
        }
        log.info("Voto registrado com sucesso. votoId={} pautaId={} sessaoId={} escolha={}",
                voto.getId(), pautaId, sessao.getId(), voto.getVotoEscolha());
        return new VotoOutput(voto.getId(), pauta.getId(), mascararCpf(voto.getAssociadoId()), voto.getVotoEscolha());
    }

}