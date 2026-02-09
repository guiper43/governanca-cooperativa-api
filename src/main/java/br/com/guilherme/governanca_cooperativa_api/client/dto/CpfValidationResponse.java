package br.com.guilherme.governanca_cooperativa_api.client.dto;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;

public record CpfValidationResponse(CpfValidationStatus status) {
}
