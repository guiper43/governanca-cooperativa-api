package br.com.guilherme.governanca_cooperativa_api.services;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Voto;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VotoService {
    private final VotoRepository votoRepository;
    private final SessaoRepository sessaoRepository;
    private final PautaService pautaService;

    public VotoResponse votar(VotoRequest request) {
        UUID pautaId = request.pautaId();

        Sessao sessao = sessaoRepository.findByPautaId(pautaId).orElseThrow();
        if (LocalDateTime.now().isAfter(sessao.getDataFechamento())) {
            throw new IllegalStateException("Sessão encerrada");
        }

        Pauta pauta = pautaService.buscarEntidade(pautaId);
        UUID id = UUID.randomUUID();
        Voto voto = Voto.criar(id, pauta, request.associadoId(), request.votoEscolha());

        try {
            votoRepository.save(voto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Associado já votou nessa sessão");
        }
        return new VotoResponse(voto.getId(), pauta.getId(), voto.getAssociadoId(), voto.getVotoEscolha());
    }
}
