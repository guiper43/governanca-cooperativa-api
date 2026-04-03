package br.com.guilherme.governanca_cooperativa_api.domain.dto;

import java.util.UUID;

public record VotoOutput(
        UUID protocolo,
        UUID pautaId,
        String status) {
}
