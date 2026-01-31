package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.client.CpfValidationClient;
import br.com.guilherme.governanca_cooperativa_api.config.CpfValidationProperties;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Voto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.utils.validation.CpfLocalValidator;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoResponse;
import feign.FeignException;
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
    private final CpfValidationClient client;
    private final CpfLocalValidator validator;
    private final CpfValidationProperties properties;

    public VotoResponse votar(UUID pautaId, VotoRequest request) {
        log.info("Iniciando voto. pautaId={}", pautaId);
        Sessao sessao = sessaoRepository.findByPautaId(pautaId)
            .orElseThrow(() -> {
                log.warn("Sessão não encontrada para pauta. pautaId={}", pautaId);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada para a pauta");
            });

        if (LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            log.warn("Sessão encerrada. pautaId={} sessaoId={} dataFechamento={}", pautaId, sessao.getId(), sessao.getDataFechamento());
            throw new BusinessException("Sessão encerrada");
        }

        CpfValidationStatus statusCpf = resolverStatusCpf(request.associadoId());

        if (statusCpf == CpfValidationStatus.UNABLE_TO_VOTE) {
            log.warn("Associado inapto a votar. pautaId={} sessaoId={} cpf={}", pautaId, sessao.getId(), mascararCpf(request.associadoId()));
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
        return new VotoResponse(voto.getId(), pauta.getId(), voto.getAssociadoId(), voto.getVotoEscolha());
    }

    private CpfValidationStatus resolverStatusCpf(String cpf) {

        if (!properties.isEnabled()) {
            log.info("Validação externa desabilitada. Usando validação local.");
            return validator.validarStatus(cpf);
        }

        try {
            return client.buscarStatusCpf(cpf).status();
        } catch (FeignException e) {

            if (properties.isFallbackEnabled()) {
                log.warn("Falha na validação externa. Acionando fallback local. httpStatus={} cpf={}", e.status(),
                    mascararCpf(cpf)
                );
                return validator.validarStatus(cpf);
            }

            switch (e.status()) {
                case 404 -> {
                    log.warn("CPF inválido pela validação externa. cpf={}", mascararCpf(cpf));
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF inválido");
                }
                default -> {
                    log.error("Validação externa  indisponível. httpStatus={}", e.status());
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Validação de CPF indisponível");
                }
            }

        }
    }

    private String mascararCpf(String cpf) {
        return cpf.substring(0, 3) + "*****" + cpf.substring(9);
    }


}