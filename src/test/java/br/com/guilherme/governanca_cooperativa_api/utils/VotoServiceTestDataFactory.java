package br.com.guilherme.governanca_cooperativa_api.utils;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoInput;

public class VotoServiceTestDataFactory {
    public static final String CPF_VALIDO = "12345678901";
    public static final String CPF_VALIDO_MASCARADO = "123*****01";
    public static final String CPF_INVALIDO = "00000000000";

    private VotoServiceTestDataFactory() {
    }

    public static VotoInput requestPadrao() {
        return request(CPF_VALIDO, VotoEscolha.SIM);
    }

    public static VotoInput request(String cpf, VotoEscolha escolha) {
        return new VotoInput(cpf, escolha);
    }

}
