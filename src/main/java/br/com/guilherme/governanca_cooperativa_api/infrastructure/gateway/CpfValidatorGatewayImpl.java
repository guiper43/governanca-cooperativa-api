package br.com.guilherme.governanca_cooperativa_api.infrastructure.gateway;

import br.com.guilherme.governanca_cooperativa_api.client.CpfValidationClient;
import br.com.guilherme.governanca_cooperativa_api.config.CpfValidationProperties;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.service.gateway.CpfValidatorGateway;
import br.com.guilherme.governanca_cooperativa_api.utils.validation.CpfLocalValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import static br.com.guilherme.governanca_cooperativa_api.utils.CpfUtils.mascararCpf;

@Component
@RequiredArgsConstructor
@Slf4j
public class CpfValidatorGatewayImpl implements CpfValidatorGateway {

    private final CpfValidationClient client;
    private final CpfLocalValidator validator;
    private final CpfValidationProperties properties;

    @Override
    public CpfValidationStatus validar(String cpf) {
        if (!properties.isEnabled()) {
            log.info("Validação externa desabilitada. Usando validação local.");
            return validator.validarStatus(cpf);
        }

        try {
            return client.buscarStatusCpf(cpf).status();
        } catch (FeignException e) {

            if (properties.isFallbackEnabled()) {
                log.warn("Falha na validação externa. Acionando fallback local. httpStatus={} cpf={}", e.status(),
                        mascararCpf(cpf));
                return validator.validarStatus(cpf);
            }

            switch (e.status()) {
                case 404 -> {
                    log.warn("CPF inválido pela validação externa. cpf={}", mascararCpf(cpf));
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF inválido");
                }
                default -> {
                    log.error("Validação externa  indisponível. httpStatus={}", e.status());
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Validação de CPF indisponível");
                }
            }
        }
    }
}
