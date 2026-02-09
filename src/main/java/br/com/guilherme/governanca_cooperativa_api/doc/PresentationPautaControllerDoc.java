package br.com.guilherme.governanca_cooperativa_api.doc;

import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Mobile - Pautas", description = "Endpoints para construção de telas mobile relacionadas a Pautas")
public interface PresentationPautaControllerDoc {

    @Operation(summary = "Retorna a definição da tela de cadastro de pauta")
    ResponseEntity<PresentationTelaResponse> getTelaCadastroPauta();
}
