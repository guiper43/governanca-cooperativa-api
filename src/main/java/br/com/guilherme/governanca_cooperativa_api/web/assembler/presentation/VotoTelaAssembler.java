package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.ItemSelecaoMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaSelecaoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class VotoTelaAssembler {

    public TelaResponse montarTelaVotacao(Pauta pauta) {
        log.debug("Montando opções de voto. pautaId={}", pauta.getId());

        String urlVoto = "/v1/pautas/" + pauta.getId() + "/votos";

        var itemSim = new ItemSelecaoMobile(
                "SIM",
                urlVoto,
                Map.of("votoEscolha", "SIM", "associadoId", ""));

        var itemNao = new ItemSelecaoMobile(
                "NÃO",
                urlVoto,
                Map.of("votoEscolha", "NAO", "associadoId", ""));

        return new TelaSelecaoResponse(
                "Votação: " + pauta.getDescricao(),
                List.of(itemSim, itemNao));
    }
}
