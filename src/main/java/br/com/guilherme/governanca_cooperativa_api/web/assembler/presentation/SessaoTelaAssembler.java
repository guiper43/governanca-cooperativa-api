package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoComponenteMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationBotaoAcao;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationComponenteVisual;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaFormularioResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SessaoTelaAssembler {

    public PresentationTelaResponse montarTelaAbertura(PautaOutput pauta) {
        log.debug("Montando estrutura visual da tela de sessão. pautaId={}, titulo={}", pauta.id(),
                pauta.descricao());

        var inputDuracao = new PresentationComponenteVisual(
                null,
                "duracaoMinutos",
                null,
                "Duração (minutos)",
                "1",
                TipoComponenteMobile.INPUT_NUMERO);

        var botaoIniciar = new PresentationBotaoAcao(
                "Iniciar Sessão",
                "/v1/pautas/" + pauta.id() + "/sessoes",
                Map.of("duracaoMinutos", ""));

        return new PresentationTelaFormularioResponse(
                "Abrir Sessão: " + pauta.descricao(),
                List.of(inputDuracao),
                botaoIniciar);
    }
}
