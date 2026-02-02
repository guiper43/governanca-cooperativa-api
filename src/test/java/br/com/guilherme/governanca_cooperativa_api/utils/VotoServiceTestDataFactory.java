package br.com.guilherme.governanca_cooperativa_api.utils;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoRequest;

public class VotoServiceTestDataFactory {
    public static final String CPF_VALIDO = "12345678901";
    public static final String CPF_INVALIDO = "00000000000";

    private VotoServiceTestDataFactory() {
    }


    public static VotoRequest requestPadrao() {
        return request(CPF_VALIDO, VotoEscolha.SIM);
    }

    public static VotoRequest request(String cpf, VotoEscolha escolha) {
        return new VotoRequest(cpf, escolha);
    }

}
