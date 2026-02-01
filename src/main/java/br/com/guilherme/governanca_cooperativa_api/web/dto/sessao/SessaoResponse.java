package br.com.guilherme.governanca_cooperativa_api.web.dto.sessao;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record SessaoResponse(
        @Schema(description = "Identificador única da sessão", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") UUID id,

        @Schema(description = "Identificador da pauta vinculada", example = "550e8400-e29b-41d4-a716-446655440000") UUID pautaId,

        @Schema(description = "Data e hora de abertura da sessão", example = "2026-01-31T10:00:00") LocalDateTime dataAbertura,

        @Schema(description = "Data e hora de encerramento da sessão", example = "2026-01-31T11:00:00") LocalDateTime dataFechamento) {
}
