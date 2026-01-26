package br.com.guilherme.governanca_cooperativa_api.web.dto.resultado;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.ResultadoStatus;

import java.util.UUID;

public record ResultadoResponse(
    UUID pautaId,
    long totalSim,
    long totalNao,
    ResultadoStatus status
) {
}
