package br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de campo de formulário")
public enum TipoCampoMobile {
    TEXT,
    NUMBER,
    DATE
}
