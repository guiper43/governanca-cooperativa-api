package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PautaService {
    private final PautaRepository repository;

    public PautaOutput criar(PautaInput input) {
        log.info("Iniciando criação de pauta. descricao='{}'", input.descricao());
        UUID id = UUID.randomUUID();
        Pauta pauta = Pauta.criar(id, input.descricao());
        repository.save(pauta);
        log.info("Pauta criada com sucesso. pautaId={}", pauta.getId());
        return new PautaOutput(pauta.getId(), pauta.getDescricao());
    }

    public Pauta buscarEntidade(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> {
                log.warn("Pauta não encontrada. id={}", id);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada");
            });
    }

    public PautaOutput buscar(UUID id) {
        Pauta pauta = buscarEntidade(id);
        return new PautaOutput(pauta.getId(), pauta.getDescricao());
    }

}
