package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoComponenteMobile;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoTelaMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationBotaoAcao;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationComponenteVisual;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaFormularioResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PautaTelaAssemblerTest {

    @InjectMocks
    private PautaTelaAssembler assembler;

    @Test
    void montarTelaCadastro_sucesso_retornaTelaComTituloEBotaoCadastro() {
        PresentationTelaResponse response = assembler.montarTelaCadastro();

        assertNotNull(response);
        assertInstanceOf(PresentationTelaFormularioResponse.class, response);

        PresentationTelaFormularioResponse telaForm = (PresentationTelaFormularioResponse) response;

        assertAll(
                () -> assertEquals("Nova Pauta", telaForm.titulo()),
                () -> assertEquals(TipoTelaMobile.FORMULARIO, telaForm.tipo()),
                () -> assertNotNull(telaForm.itens()),
                () -> assertEquals(1, telaForm.itens().size()),
                () -> assertNotNull(telaForm.botaoOk()));

        PresentationComponenteVisual input = telaForm.itens().get(0);
        assertAll(
                () -> assertEquals("descricao", input.idCampoTexto()),
                () -> assertEquals("Assunto da Pauta", input.titulo()),
                () -> assertEquals(TipoComponenteMobile.INPUT_TEXTO, input.tipo()));

        PresentationBotaoAcao botao = telaForm.botaoOk();
        assertAll(
                () -> assertEquals("Cadastrar Pauta", botao.texto()),
                () -> assertEquals("/v1/pautas", botao.url()),
                () -> assertNotNull(botao.body()),
                () -> assertTrue(botao.body().containsKey("descricao")));
    }
}
