package br.com.guilherme.governanca_cooperativa_api.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoOutput(
        UUID id,
        UUID pautaId,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento) {
}
