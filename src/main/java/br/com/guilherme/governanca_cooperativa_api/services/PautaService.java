package br.com.guilherme.governanca_cooperativa_api.services;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.PautaRepository;
import br.com.guilherme.governanca_cooperativa_api.web.dto.pauta.PautaRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.pauta.PautaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PautaService {
    private final PautaRepository repository;

    public PautaResponse criar(PautaRequest request) {
        UUID id = UUID.randomUUID();
        Pauta pauta = Pauta.criar(id, request.descricao());
        repository.save(pauta);
        return new PautaResponse(pauta.getId(), pauta.getDescricao());
    }

    public Pauta buscarEntidade(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));
    }

    public PautaResponse buscar(UUID id) {
        Pauta pauta = buscarEntidade(id);
        return new PautaResponse(pauta.getId(), pauta.getDescricao());
    }

}
