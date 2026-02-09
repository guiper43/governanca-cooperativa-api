package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.pauta;
import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.uuid;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository repository;

    @InjectMocks
    private PautaService service;

    @Test
    void criar_requestValido_salvaPautaERetornaResponse() {
        PautaInput input = new PautaInput("Pauta teste");
        ArgumentCaptor<Pauta> pautaCaptor = ArgumentCaptor.forClass(Pauta.class);

        PautaOutput response = service.criar(input);

        verify(repository).save(pautaCaptor.capture());
        verifyNoMoreInteractions(repository);

        Pauta pautaSalva = pautaCaptor.getValue();

        assertAll(
                () -> assertNotNull(response),
                () -> assertNotNull(response.id()),
                () -> assertEquals("Pauta teste", response.descricao()),
                () -> assertNotNull(pautaSalva),
                () -> assertEquals(response.id(), pautaSalva.getId()),
                () -> assertEquals("Pauta teste", pautaSalva.getDescricao()));
    }

    @Test
    void criar_repositoryFalha_propagExcecao() {
        PautaInput input = new PautaInput("Erro DB");

        doThrow(new RuntimeException("db down")).when(repository).save(any(Pauta.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.criar(input));

        verify(repository).save(any(Pauta.class));
        verifyNoMoreInteractions(repository);

        assertEquals("db down", ex.getMessage());
    }

    @Test
    void buscar_quandoExiste_retornaResponse() {
        UUID pautaId = uuid("33333333-3333-3333-3333-333333333333");
        Pauta pauta = pauta(pautaId, "Pauta X");

        when(repository.findById(pautaId)).thenReturn(Optional.of(pauta));

        PautaOutput response = service.buscar(pautaId);

        verify(repository).findById(pautaId);
        verifyNoMoreInteractions(repository);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(pautaId, response.id()),
                () -> assertEquals("Pauta X", response.descricao()));
    }

    @Test
    void buscar_quandoNaoExiste_lancaNotFound() {
        UUID pautaId = uuid("44444444-4444-4444-4444-444444444444");

        when(repository.findById(pautaId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.buscar(pautaId));

        verify(repository).findById(pautaId);
        verifyNoMoreInteractions(repository);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode()),
                () -> assertEquals("Pauta não encontrada", ex.getReason()));
    }
}
