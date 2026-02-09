package br.com.guilherme.governanca_cooperativa_api.web.dto.presentation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Definição de botão de ação")
public record PresentationBotaoAcao(
    @Schema(description = "Texto do botão", example = "Confirmar") String texto,

    @Schema(description = "URL de destino", example = "/v1/recurso") String url,

    @Schema(description = "Corpo da requisição (opcional)") Map<String, Object> body) {
}
