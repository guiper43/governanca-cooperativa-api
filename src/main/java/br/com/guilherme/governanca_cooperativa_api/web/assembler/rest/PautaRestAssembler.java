package br.com.guilherme.governanca_cooperativa_api.web.assembler.rest;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaOutput;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta.PautaRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta.PautaResponse;
import org.springframework.stereotype.Component;

@Component
public class PautaRestAssembler {

    public PautaInput toInput(PautaRequest request) {
        return new PautaInput(request.descricao());
    }

    public PautaResponse toResponse(PautaOutput output) {
        return new PautaResponse(output.id(), output.descricao());
    }
}
