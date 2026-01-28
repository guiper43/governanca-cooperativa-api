package br.com.guilherme.governanca_cooperativa_api.web.dto.voto;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VotoRequest(
    @NotBlank
    @Pattern(regexp = "\\d{11}")
    String associadoId,
    @NotNull
    VotoEscolha votoEscolha
) {
}
