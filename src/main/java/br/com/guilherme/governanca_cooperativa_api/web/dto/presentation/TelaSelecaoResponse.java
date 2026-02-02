package br.com.guilherme.governanca_cooperativa_api.web.dto.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoTelaMobile;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Tela do tipo SELECAO")
public record TelaSelecaoResponse(
        String titulo,

        @Schema(defaultValue = "SELECAO") TipoTelaMobile tipo,

        List<ItemSelecaoMobile> itens) implements TelaResponse {

    public TelaSelecaoResponse(String titulo, List<ItemSelecaoMobile> itens) {
        this(titulo, TipoTelaMobile.SELECAO, itens);
    }
}
