package br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipos de componentes visuais suportados")
public enum TipoComponenteMobile {
    TEXTO,
    INPUT_TEXTO,
    INPUT_NUMERO,
    INPUT_DATA
}
