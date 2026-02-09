package br.com.guilherme.governanca_cooperativa_api.domain.dto;

import java.util.UUID;

public record PautaOutput(
    UUID id,
    String descricao) {
}
