package br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record VotoResponse(
        @Schema(description = "Identificador do voto registrado", example = "789e8400-e29b-41d4-a716-446655440789") UUID id,

        @Schema(description = "Identificador da pauta votada", example = "550e8400-e29b-41d4-a716-446655440000") UUID pautaId,

        @Schema(description = "CPF do associado (mascarado)", example = "123.***.***-01") String associadoId,

        @Schema(description = "Opção colhida. Valores: SIM, NAO", example = "SIM") VotoEscolha votoEscolha) {
}
