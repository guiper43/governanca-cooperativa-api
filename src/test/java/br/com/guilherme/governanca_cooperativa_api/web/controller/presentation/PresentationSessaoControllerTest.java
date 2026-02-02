package br.com.guilherme.governanca_cooperativa_api.web.controller.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation.SessaoTelaAssembler;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaFormularioResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.pauta;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresentationSessaoControllerTest {

    @Mock
    private PautaService pautaService;

    @Mock
    private SessaoTelaAssembler assembler;

    @InjectMocks
    private PresentationSessaoController controller;

    @Test
    void getTelaAberturaSessao_sucesso_retornaStatusOk() {
        UUID pautaId = UUID.randomUUID();
        Pauta pautaMock = pauta(pautaId, "Pauta");
        TelaResponse telaConcrete = new TelaFormularioResponse("Titulo", Collections.emptyList(), null);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pautaMock);
        when(assembler.montarTelaAbertura(pautaMock)).thenReturn(telaConcrete);

        ResponseEntity<TelaResponse> response = controller.getTelaAberturaSessao(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(assembler).montarTelaAbertura(pautaMock);
        verifyNoMoreInteractions(pautaService, assembler);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertSame(telaConcrete, response.getBody()));
    }
}
