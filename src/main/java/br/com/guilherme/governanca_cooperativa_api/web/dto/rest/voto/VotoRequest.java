package br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.VotoEscolha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

public record VotoRequest(
        @Schema(description = "CPF do associado (apenas números)", example = "12345678901", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank @Pattern(regexp = "\\d{11}") String associadoId,

        @Schema(description = "Escolha do voto. Valores aceitos: SIM, NAO", example = "SIM", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull VotoEscolha votoEscolha) {
}
