package br.com.guilherme.governanca_cooperativa_api.services;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.web.dto.sessao.SessaoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.sessao.SessaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessaoService {
    private final SessaoRepository repository;
    private final PautaService pautaService;

    public SessaoResponse abrir(SessaoRequest request) {
        UUID pautaId = request.pautaId();
        if (repository.findByPautaId(pautaId).isPresent()) {
            throw new IllegalStateException("Sessão já existe para a pauta");
        }
        Pauta pauta = pautaService.buscarEntidade(pautaId);
        int duracao = request.duracaoMinutos() == null ? 1 : request.duracaoMinutos();
        LocalDateTime abertura = LocalDateTime.now();
        LocalDateTime fechamento = abertura.plusMinutes(duracao);
        UUID id = UUID.randomUUID();
        Sessao sessao = Sessao.criar(id, pauta, abertura, fechamento);
        repository.save(sessao);
return new SessaoResponse(sessao.getId(), pauta.getId(), sessao.getDataAbertura(), sessao.getDataFechamento());
}

