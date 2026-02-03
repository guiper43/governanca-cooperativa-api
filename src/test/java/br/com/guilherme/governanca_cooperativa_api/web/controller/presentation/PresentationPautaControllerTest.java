package br.com.guilherme.governanca_cooperativa_api.web.controller.presentation;

import br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation.PautaTelaAssembler;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaFormularioResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresentationPautaControllerTest {

    @Mock
    private PautaTelaAssembler assembler;

    @InjectMocks
    private PresentationPautaController controller;

    @Test
    void getTelaCadastroPauta_sucesso_retornaStatusOkEAcaoDoAssembler() {
        PresentationTelaResponse telaConcrete = new PresentationTelaFormularioResponse("Titulo", Collections.emptyList(), null);
        when(assembler.montarTelaCadastro()).thenReturn(telaConcrete);

        ResponseEntity<PresentationTelaResponse> response = controller.getTelaCadastroPauta();

        verify(assembler).montarTelaCadastro();
        verifyNoMoreInteractions(assembler);

        assertAll(
            () -> assertNotNull(response),
            () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
            () -> assertSame(telaConcrete, response.getBody()));
    }
}
