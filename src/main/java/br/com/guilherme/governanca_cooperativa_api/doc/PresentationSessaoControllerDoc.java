package br.com.guilherme.governanca_cooperativa_api.doc;

import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Mobile - Sessões", description = "Endpoints para construção de telas mobile relacionadas a Sessões")
public interface PresentationSessaoControllerDoc {

    @Operation(summary = "Retorna a definição da tela de abertura de sessão")
    ResponseEntity<PresentationTelaResponse> getTelaAberturaSessao(UUID pautaId);
}
