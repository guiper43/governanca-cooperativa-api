package br.com.guilherme.governanca_cooperativa_api.web.dto.voto;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;

import java.util.UUID;

public record VotoResponse(
    UUID id,
    UUID pautaId,
    String associadoId,
    VotoEscolha votoEscolha
) {
}
