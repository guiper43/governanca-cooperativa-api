package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
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

import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.pauta;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SessaoTelaAssemblerTest {

    @InjectMocks
    private SessaoTelaAssembler assembler;

    @Test
    void montarTelaAbertura_sucesso_retornaTelaComNomeDaPauta() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = pauta(pautaId, "Pauta Teste Assemble");

        TelaResponse response = assembler.montarTelaAbertura(pauta);

        assertNotNull(response);
        assertInstanceOf(TelaFormularioResponse.class, response);

        TelaFormularioResponse telaForm = (TelaFormularioResponse) response;

        assertAll(
                () -> assertEquals("Abrir Sessão: Pauta Teste Assemble", telaForm.titulo()),
                () -> assertEquals(TipoTelaMobile.FORMULARIO, telaForm.tipo()),
                () -> assertNotNull(telaForm.itens()),
                () -> assertEquals(1, telaForm.itens().size()),
                () -> assertNotNull(telaForm.botaoOk()));

        ComponenteVisualMobile input = telaForm.itens().get(0);
        assertAll(
                () -> assertEquals("duracaoMinutos", input.id()),
                () -> assertEquals("1", input.valor()),
                () -> assertEquals(TipoComponenteMobile.INPUT_NUMERO, input.tipo()));

        BotaoAcaoMobile botao = telaForm.botaoOk();
        assertAll(
                () -> assertEquals("Iniciar Sessão", botao.texto()),
                () -> assertEquals("/v1/pautas/" + pautaId + "/sessoes", botao.url()),
                () -> assertNotNull(botao.body()),
                () -> assertTrue(botao.body().containsKey("duracaoMinutos")));
    }
}
