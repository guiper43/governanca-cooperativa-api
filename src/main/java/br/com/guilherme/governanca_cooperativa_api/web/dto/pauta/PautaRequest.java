package br.com.guilherme.governanca_cooperativa_api.web.dto.pauta;

import jakarta.validation.constraints.NotBlank;

public record PautaRequest(@NotBlank String descricao) {
}
