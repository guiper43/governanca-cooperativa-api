package br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PautaRequest(
    @Schema(description = "Descrição/Tema da pauta", example = "Reforma do Estatuto", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String descricao) {
}
