package br.com.guilherme.governanca_cooperativa_api.web.assembler.rest;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoOutput;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoResponse;
import org.springframework.stereotype.Component;

import static br.com.guilherme.governanca_cooperativa_api.utils.CpfUtils.mascararCpf;

@Component
public class VotoRestAssembler {

    public VotoInput toInput(VotoRequest request) {
        return new VotoInput(request.associadoId(), request.votoEscolha());
    }

    public VotoResponse toResponse(VotoOutput output) {
        return new VotoResponse(
            output.id(),
            output.pautaId(),
            mascararCpf(output.associadoId()),
            output.votoEscolha());
    }
}
