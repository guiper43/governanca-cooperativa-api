package br.com.guilherme.governanca_cooperativa_api.doc;

import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Mobile - Votos", description = "Endpoints para construção de telas mobile relacionadas a Votos")
public interface PresentationVotoControllerDoc {

    @Operation(summary = "Retorna a lista de opções de voto para uma pauta")
    ResponseEntity<PresentationTelaResponse> getTelaVotacao(UUID pautaId);
}
