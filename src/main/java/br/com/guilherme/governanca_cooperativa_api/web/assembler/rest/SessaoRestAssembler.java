package br.com.guilherme.governanca_cooperativa_api.web.assembler.rest;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.SessaoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.SessaoOutput;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.sessao.SessaoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.sessao.SessaoResponse;
import org.springframework.stereotype.Component;

@Component
public class SessaoRestAssembler {

    public SessaoInput toInput(SessaoRequest request) {
        Integer duracao = (request != null) ? request.duracaoMinutos() : null;
        return new SessaoInput(duracao);
    }

    public SessaoResponse toResponse(SessaoOutput output) {
        return new SessaoResponse(
            output.id(),
            output.pautaId(),
            output.dataAbertura(),
            output.dataFechamento());
    }
}
