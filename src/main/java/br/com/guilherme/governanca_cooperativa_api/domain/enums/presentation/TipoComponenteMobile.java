package br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipos de componentes visuais suportados", type = "string", allowableValues = {
        "texto", "INPUT_TEXTO", "INPUT_NUMERO", "INPUT_DATA" })
public enum TipoComponenteMobile {
    @JsonProperty("texto")
    TEXTO,
    INPUT_TEXTO,
    INPUT_NUMERO,
    INPUT_DATA
}
