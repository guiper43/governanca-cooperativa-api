package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

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
public class PautaTelaAssembler {

    public PresentationTelaResponse montarTelaCadastro() {
        log.debug("Montando estrutura visual da tela de cadastro de pauta");

        var inputDescricao = new PresentationComponenteVisual(
            "descricao",
            "Assunto da Pauta",
            null,
            TipoComponenteMobile.INPUT_TEXTO);

        var botaoCadastrar = new PresentationBotaoAcao(
            "Cadastrar Pauta",
            "/v1/pautas",
            Map.of("descricao", ""));

        return new PresentationTelaFormularioResponse(
            "Nova Pauta",
            List.of(inputDescricao),
            botaoCadastrar);
    }
}
