package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

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
public class PautaTelaAssembler {

        public TelaResponse montarTelaCadastro() {
                log.debug("Montando estrutura visual da tela de cadastro de pauta");

                var inputDescricao = new ComponenteVisualMobile(
                                "descricao",
                                "Assunto da Pauta",
                                null,
                                TipoComponenteMobile.INPUT_TEXTO);

                var botaoCadastrar = new BotaoAcaoMobile(
                                "Cadastrar Pauta",
                                "/v1/pautas",
                                Map.of("descricao", ""));

                return new TelaFormularioResponse(
                                "Nova Pauta",
                                List.of(inputDescricao),
                                botaoCadastrar);
        }
}
