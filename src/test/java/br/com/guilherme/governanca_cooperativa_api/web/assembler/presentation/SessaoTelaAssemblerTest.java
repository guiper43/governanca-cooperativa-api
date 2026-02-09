package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaOutput;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SessaoTelaAssemblerTest {

        @InjectMocks
        private SessaoTelaAssembler assembler;

        @Test
        void montarTelaAbertura_sucesso_retornaTelaComNomeDaPauta() {
                UUID pautaId = UUID.randomUUID();
                PautaOutput pauta = new PautaOutput(pautaId, "Pauta Teste Assemble");

                PresentationTelaResponse response = assembler.montarTelaAbertura(pauta);

                assertNotNull(response);
                assertInstanceOf(PresentationTelaFormularioResponse.class, response);

                PresentationTelaFormularioResponse telaForm = (PresentationTelaFormularioResponse) response;

                assertAll(
                                () -> assertEquals("Abrir Sessão: Pauta Teste Assemble", telaForm.titulo()),
                                () -> assertEquals(TipoTelaMobile.FORMULARIO, telaForm.tipo()),
                                () -> assertNotNull(telaForm.itens()),
                                () -> assertEquals(1, telaForm.itens().size()),
                                () -> assertNotNull(telaForm.botaoOk()));

                PresentationComponenteVisual input = telaForm.itens().get(0);
                assertAll(
                                () -> assertEquals("duracaoMinutos", input.idCampoNumerico()),
                                () -> assertEquals("1", input.valor()),
                                () -> assertEquals(TipoComponenteMobile.INPUT_NUMERO, input.tipo()));

                PresentationBotaoAcao botao = telaForm.botaoOk();
                assertAll(
                                () -> assertEquals("Iniciar Sessão", botao.texto()),
                                () -> assertEquals("/v1/pautas/" + pautaId + "/sessoes", botao.url()),
                                () -> assertNotNull(botao.body()),
                                () -> assertTrue(botao.body().containsKey("duracaoMinutos")));
        }
}
