package br.com.guilherme.governanca_cooperativa_api.web.dto.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoTelaMobile;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Tela do tipo FORMULARIO")
public record TelaFormularioResponse(
        String titulo,

        @Schema(defaultValue = "FORMULARIO") TipoTelaMobile tipo,

        List<ComponenteVisualMobile> itens,

        BotaoAcaoMobile botaoOk,

        BotaoAcaoMobile botaoCancelar) implements TelaResponse {

    public TelaFormularioResponse(String titulo, List<ComponenteVisualMobile> itens, BotaoAcaoMobile botaoOk) {
        this(titulo, TipoTelaMobile.FORMULARIO, itens, botaoOk, null);
    }

    public TelaFormularioResponse(String titulo, List<ComponenteVisualMobile> itens, BotaoAcaoMobile botaoOk,
            BotaoAcaoMobile botaoCancelar) {
        this(titulo, TipoTelaMobile.FORMULARIO, itens, botaoOk, botaoCancelar);
    }
}
