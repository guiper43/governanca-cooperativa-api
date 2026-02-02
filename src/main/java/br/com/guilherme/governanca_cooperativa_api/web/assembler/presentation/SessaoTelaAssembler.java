package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoComponenteMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.BotaoAcaoMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.ComponenteVisualMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaFormularioResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SessaoTelaAssembler {

    public TelaResponse montarTelaAbertura(Pauta pauta) {
        log.debug("Montando estrutura visual da tela de sessão. pautaId={}, titulo={}", pauta.getId(),
                pauta.getDescricao());

        var inputDuracao = new ComponenteVisualMobile(
                "duracaoMinutos",
                "Duração (minutos)",
                "1",
                TipoComponenteMobile.INPUT_NUMERO);

        var botaoIniciar = new BotaoAcaoMobile(
                "Iniciar Sessão",
                "/v1/pautas/" + pauta.getId() + "/sessoes",
                Map.of("duracaoMinutos", ""));

        return new TelaFormularioResponse(
                "Abrir Sessão: " + pauta.getDescricao(),
                List.of(inputDuracao),
                botaoIniciar);
    }
}
