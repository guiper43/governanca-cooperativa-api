package br.com.guilherme.governanca_cooperativa_api.web.dto.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoTelaMobile;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Tela do tipo SELECAO")
public record PresentationTelaSelecaoResponse(
        String titulo,

        @Schema(defaultValue = "SELECAO") TipoTelaMobile tipo,

        List<PresentationItemSelecao> itens) implements PresentationTelaResponse {

    public PresentationTelaSelecaoResponse(String titulo, List<PresentationItemSelecao> itens) {
        this(titulo, TipoTelaMobile.SELECAO, itens);
    }
}
