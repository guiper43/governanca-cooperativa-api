package br.com.guilherme.governanca_cooperativa_api.web.dto.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoTelaMobile;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Tela do tipo FORMULARIO")
public record PresentationTelaFormularioResponse(
        String titulo,

        @Schema(defaultValue = "FORMULARIO") TipoTelaMobile tipo,

        List<PresentationComponenteVisual> itens,

        PresentationBotaoAcao botaoOk,

        PresentationBotaoAcao botaoCancelar) implements PresentationTelaResponse {

    public PresentationTelaFormularioResponse(String titulo, List<PresentationComponenteVisual> itens, PresentationBotaoAcao botaoOk) {
        this(titulo, TipoTelaMobile.FORMULARIO, itens, botaoOk, null);
    }

    public PresentationTelaFormularioResponse(String titulo, List<PresentationComponenteVisual> itens, PresentationBotaoAcao botaoOk,
                                              PresentationBotaoAcao botaoCancelar) {
        this(titulo, TipoTelaMobile.FORMULARIO, itens, botaoOk, botaoCancelar);
    }
}
