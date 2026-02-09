package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaOutput;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationItemSelecao;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaSelecaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class VotoTelaAssembler {

    public PresentationTelaResponse montarTelaVotacao(PautaOutput pauta) {
        log.debug("Montando opções de voto. pautaId={}", pauta.id());

        String urlVoto = "/v1/pautas/" + pauta.id() + "/votos";

        var itemSim = new PresentationItemSelecao(
            "SIM",
            urlVoto,
            Map.of("votoEscolha", "SIM", "associadoId", ""));

        var itemNao = new PresentationItemSelecao(
            "NÃO",
            urlVoto,
            Map.of("votoEscolha", "NAO", "associadoId", ""));

        return new PresentationTelaSelecaoResponse(
            "Votação: " + pauta.descricao(),
            List.of(itemSim, itemNao));
    }
}
