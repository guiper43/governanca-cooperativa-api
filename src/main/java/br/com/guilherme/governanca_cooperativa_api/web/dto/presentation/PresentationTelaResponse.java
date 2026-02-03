package br.com.guilherme.governanca_cooperativa_api.web.dto.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoTelaMobile;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo", visible = true)
@JsonSubTypes({
                @JsonSubTypes.Type(value = PresentationTelaFormularioResponse.class, name = "FORMULARIO"),
                @JsonSubTypes.Type(value = PresentationTelaSelecaoResponse.class, name = "SELECAO")
})
@Schema(description = "Contrato base de tela", discriminatorProperty = "tipo")
public sealed interface PresentationTelaResponse permits PresentationTelaFormularioResponse, PresentationTelaSelecaoResponse {
        String titulo();

        TipoTelaMobile tipo();
}
