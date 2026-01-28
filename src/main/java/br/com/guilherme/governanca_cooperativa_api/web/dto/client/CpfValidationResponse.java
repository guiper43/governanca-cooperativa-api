package br.com.guilherme.governanca_cooperativa_api.web.dto.client;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;

public record CpfValidationResponse(CpfValidationStatus status) {
}
