package br.com.guilherme.governanca_cooperativa_api.domain.dto;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.ResultadoStatus;

import java.util.UUID;

public record ResultadoOutput(
        UUID pautaId,
        Long votosSim,
        Long votosNao,
        ResultadoStatus status) {
}
