package br.com.guilherme.governanca_cooperativa_api.web.dto.rest.resultado;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.ResultadoStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ResultadoResponse(
        @Schema(description = "Identificador da pauta consultada", example = "550e8400-e29b-41d4-a716-446655440000") UUID pautaId,

        @Schema(description = "Total de votos SIM. Nulo enquanto a sessao estiver em andamento.", example = "150", nullable = true) Long totalSim,

        @Schema(description = "Total de votos NAO. Nulo enquanto a sessao estiver em andamento.", example = "75", nullable = true) Long totalNao,

        @Schema(description = "Status final da votacao. Valores: APROVADA, REPROVADA, EMPATE, EM_ANDAMENTO", example = "APROVADA") ResultadoStatus status) {
}
