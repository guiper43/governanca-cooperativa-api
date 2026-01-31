package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.web.dto.sessao.SessaoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.sessao.SessaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessaoService {
    private final SessaoRepository repository;
    private final PautaService pautaService;

    public SessaoResponse abrir(UUID pautaId, SessaoRequest request) {
        int duracao = request.duracaoMinutos() == null ? 1 : request.duracaoMinutos();

        if (duracao <= 0) {
            throw new BusinessException("Duração inválida");
        }

        if (repository.findByPautaId(pautaId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sessão já existe para a pauta");
        }


        Pauta pauta = pautaService.buscarEntidade(pautaId);


        LocalDateTime abertura = LocalDateTime.now();
        LocalDateTime fechamento = abertura.plusMinutes(duracao);
        UUID id = UUID.randomUUID();
        Sessao sessao = Sessao.criar(id, pauta, abertura, fechamento);
        repository.save(sessao);
        return new SessaoResponse(sessao.getId(), pauta.getId(), sessao.getDataAbertura(), sessao.getDataFechamento());
    }

}