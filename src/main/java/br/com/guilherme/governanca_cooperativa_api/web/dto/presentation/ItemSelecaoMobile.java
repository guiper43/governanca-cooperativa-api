package br.com.guilherme.governanca_cooperativa_api.web.dto.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Item de lista de seleção")
public record ItemSelecaoMobile(
        @Schema(description = "Texto exibido", example = "Sim") String texto,

        @Schema(description = "URL para ação de seleção", example = "/v1/votos") String url,

        @Schema(description = "Corpo da requisição") Map<String, Object> body) {
}
