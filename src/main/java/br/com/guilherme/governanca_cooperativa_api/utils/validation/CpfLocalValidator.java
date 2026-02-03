package br.com.guilherme.governanca_cooperativa_api.utils.validation;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.guilherme.governanca_cooperativa_api.config.CpfValidationProperties;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.CpfValidationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class CpfLocalValidator {
    private final CpfValidationProperties properties;

    private final CPFValidator validadorStella = new CPFValidator();

    public CpfValidationStatus validarStatus(String cpf) {
        try {
            validadorStella.assertValid(cpf);
        } catch (InvalidStateException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF inválido");
        }

        Set<String> cpfsInaptos = properties.unableCpfs();

        if (cpfsInaptos != null && cpfsInaptos.contains(cpf)) {
            return CpfValidationStatus.UNABLE_TO_VOTE;
        }

        return CpfValidationStatus.ABLE_TO_VOTE;
    }

}