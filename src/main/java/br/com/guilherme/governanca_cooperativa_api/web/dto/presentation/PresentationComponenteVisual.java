package br.com.guilherme.governanca_cooperativa_api.web.dto.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoComponenteMobile;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Componente visual genérico para formulários")
public record PresentationComponenteVisual(
        @Schema(description = "Identificador do campo", example = "descricao") String id,

        @Schema(description = "Título ou Label", example = "Descrição da Pauta") String titulo,

        @Schema(description = "Valor inicial ou pré-preenchido") String valor,

        @Schema(description = "Tipo do componente") TipoComponenteMobile tipo) {
}
