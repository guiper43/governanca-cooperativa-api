package br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record VotoResponse(
        @Schema(description = "Protocolo tecnico de confirmacao do registro", example = "789e8400-e29b-41d4-a716-446655440789") UUID protocolo,

        @Schema(description = "Identificador da pauta votada", example = "550e8400-e29b-41d4-a716-446655440000") UUID pautaId,

        @Schema(description = "Status do recebimento do voto", example = "REGISTRADO") String status) {
}
