package br.com.guilherme.governanca_cooperativa_api.web.controller.presentation;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation.VotoTelaAssembler;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaSelecaoResponse;
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
class PresentationVotoControllerTest {

    @Mock
    private PautaService pautaService;

    @Mock
    private VotoTelaAssembler assembler;

    @InjectMocks
    private PresentationVotoController controller;

    @Test
    void getTelaVotacao_sucesso_retornaStatusOk() {
        UUID pautaId = UUID.randomUUID();
        Pauta pautaMock = pauta(pautaId, "Pauta");
        TelaResponse telaConcrete = new TelaSelecaoResponse("Titulo", Collections.emptyList());

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pautaMock);
        when(assembler.montarTelaVotacao(pautaMock)).thenReturn(telaConcrete);

        ResponseEntity<TelaResponse> response = controller.getTelaVotacao(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(assembler).montarTelaVotacao(pautaMock);
        verifyNoMoreInteractions(pautaService, assembler);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertSame(telaConcrete, response.getBody()));
    }
}
