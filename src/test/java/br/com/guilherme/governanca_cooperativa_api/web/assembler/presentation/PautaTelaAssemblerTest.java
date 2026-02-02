package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoComponenteMobile;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoTelaMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.BotaoAcaoMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.ComponenteVisualMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaFormularioResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PautaTelaAssemblerTest {

    @InjectMocks
    private PautaTelaAssembler assembler;

    @Test
    void montarTelaCadastro_sucesso_retornaTelaComTituloEBotaoCadastro() {
        TelaResponse response = assembler.montarTelaCadastro();

        assertNotNull(response);
        assertInstanceOf(TelaFormularioResponse.class, response);

        TelaFormularioResponse telaForm = (TelaFormularioResponse) response;

        assertAll(
                () -> assertEquals("Nova Pauta", telaForm.titulo()),
                () -> assertEquals(TipoTelaMobile.FORMULARIO, telaForm.tipo()),
                () -> assertNotNull(telaForm.itens()),
                () -> assertEquals(1, telaForm.itens().size()),
                () -> assertNotNull(telaForm.botaoOk()));

        ComponenteVisualMobile input = telaForm.itens().get(0);
        assertAll(
                () -> assertEquals("descricao", input.id()),
                () -> assertEquals("Assunto da Pauta", input.titulo()),
                () -> assertEquals(TipoComponenteMobile.INPUT_TEXTO, input.tipo()));

        BotaoAcaoMobile botao = telaForm.botaoOk();
        assertAll(
                () -> assertEquals("Cadastrar Pauta", botao.texto()),
                () -> assertEquals("/v1/pautas", botao.url()),
                () -> assertNotNull(botao.body()),
                () -> assertTrue(botao.body().containsKey("descricao")));
    }
}
