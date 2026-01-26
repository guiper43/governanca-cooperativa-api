package br.com.guilherme.governanca_cooperativa_api.web.dto.sessao;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoResponse(
    UUID id,
    UUID pautaId,
    LocalDateTime dataAbertura,
    LocalDateTime dataFechamento
) {
}
