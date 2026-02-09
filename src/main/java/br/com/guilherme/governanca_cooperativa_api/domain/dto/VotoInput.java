package br.com.guilherme.governanca_cooperativa_api.domain.dto;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;

public record VotoInput(
        String associadoId,
        VotoEscolha votoEscolha) {
}
