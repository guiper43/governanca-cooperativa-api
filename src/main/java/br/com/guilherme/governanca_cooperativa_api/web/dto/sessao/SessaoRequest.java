package br.com.guilherme.governanca_cooperativa_api.web.dto.sessao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SessaoRequest(
        @Schema(description = "Duração da sessão em minutos. Se nulo, assume o padrão de 1 minuto.", example = "60", requiredMode = Schema.RequiredMode.NOT_REQUIRED) Integer duracaoMinutos) {
}
