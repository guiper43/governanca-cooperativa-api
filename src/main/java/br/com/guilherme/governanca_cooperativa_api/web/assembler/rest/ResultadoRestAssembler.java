package br.com.guilherme.governanca_cooperativa_api.web.assembler.rest;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.ResultadoOutput;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.resultado.ResultadoResponse;
import org.springframework.stereotype.Component;

@Component
public class ResultadoRestAssembler {

    public ResultadoResponse toResponse(ResultadoOutput output) {
        return new ResultadoResponse(
            output.pautaId(),
            output.votosSim(),
            output.votosNao(),
            output.status());
    }
}
