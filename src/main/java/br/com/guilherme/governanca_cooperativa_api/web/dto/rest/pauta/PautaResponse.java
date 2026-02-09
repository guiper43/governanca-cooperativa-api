package br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PautaResponse(
    @Schema(description = "Identificador único da pauta", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,

    @Schema(description = "Descrição da pauta", example = "Reforma do Estatuto") String descricao) {
}
