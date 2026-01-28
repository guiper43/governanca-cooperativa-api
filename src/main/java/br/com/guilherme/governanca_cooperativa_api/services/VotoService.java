package br.com.guilherme.governanca_cooperativa_api.services;

import br.com.guilherme.governanca_cooperativa_api.client.CpfValidationClient;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Voto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VotoService {
    private final VotoRepository votoRepository;
    private final SessaoRepository sessaoRepository;
    private final PautaService pautaService;
    private final CpfValidationClient client;

    public VotoResponse votar(UUID pautaId, VotoRequest request) {

        Sessao sessao = sessaoRepository.findByPautaId(pautaId)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada para a pauta"));


        if (LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            throw new BusinessException("Sessão encerrada");
        }

        try {
            var status = client.buscarStatusCpf(request.associadoId()).status();
            if (status == CpfValidationStatus.UNABLE_TO_VOTE) {
                throw new BusinessException("CPF não está apto a votar");
            }
        } catch (FeignException.NotFound e) {
            throw e;
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Validação de CPF indisponível");
        }
        Pauta pauta = pautaService.buscarEntidade(pautaId);
        UUID id = UUID.randomUUID();
        Voto voto = Voto.criar(id, pauta, request.associadoId(), request.votoEscolha());

        try {
            votoRepository.save(voto);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Associado já votou nessa sessão");
        }
        return new VotoResponse(voto.getId(), pauta.getId(), voto.getAssociadoId(), voto.getVotoEscolha());
    }
}
