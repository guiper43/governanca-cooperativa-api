package br.com.guilherme.governanca_cooperativa_api.web.dto.voto;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VotoRequest(
    @NotNull
    UUID pautaId,
    @NotBlank
    String associadoId,
    @NotNull
    VotoEscolha votoEscolha
    ) {
}
