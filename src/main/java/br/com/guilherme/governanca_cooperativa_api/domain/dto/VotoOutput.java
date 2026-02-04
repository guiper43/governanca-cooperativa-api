package br.com.guilherme.governanca_cooperativa_api.domain.dto;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.VotoEscolha;
import java.util.UUID;

public record VotoOutput(
        UUID id,
        UUID pautaId,
        String associadoId,
        VotoEscolha votoEscolha) {
}
