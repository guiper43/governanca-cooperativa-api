package br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoTelaMobile;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationItemSelecao;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaSelecaoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.pauta;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VotoTelaAssemblerTest {

    @InjectMocks
    private VotoTelaAssembler assembler;

    @Test
    void montarTelaVotacao_sucesso_retornaOpcoesSimNaoComContrato() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = pauta(pautaId, "Pauta Votação");

        PresentationTelaResponse response = assembler.montarTelaVotacao(pauta);

        assertNotNull(response);
        assertInstanceOf(PresentationTelaSelecaoResponse.class, response);

        PresentationTelaSelecaoResponse telaSel = (PresentationTelaSelecaoResponse) response;

        assertAll(
            () -> assertEquals("Votação: Pauta Votação", telaSel.titulo()),
            () -> assertEquals(TipoTelaMobile.SELECAO, telaSel.tipo()),
            () -> assertNotNull(telaSel.itens()),
            () -> assertEquals(2, telaSel.itens().size()));

        PresentationItemSelecao itemSim = telaSel.itens().get(0);
        assertAll(
            () -> assertEquals("SIM", itemSim.texto()),
            () -> assertEquals("/v1/pautas/" + pautaId + "/votos", itemSim.url()),
            () -> assertEquals("SIM", itemSim.body().get("votoEscolha")),
            () -> assertTrue(itemSim.body().containsKey("associadoId")));

        PresentationItemSelecao itemNao = telaSel.itens().get(1);
        assertAll(
            () -> assertEquals("NÃO", itemNao.texto()),
            () -> assertEquals("NAO", itemNao.body().get("votoEscolha")),
            () -> assertTrue(itemNao.body().containsKey("associadoId")));
    }
}
