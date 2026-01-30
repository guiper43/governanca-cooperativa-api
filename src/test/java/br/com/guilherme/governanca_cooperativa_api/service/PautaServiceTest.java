package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.PautaRepository;
import br.com.guilherme.governanca_cooperativa_api.services.PautaService;
import br.com.guilherme.governanca_cooperativa_api.web.dto.pauta.PautaRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.pauta.PautaResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PautaServiceTest {
    @Mock
        private PautaRepository repository;

    @InjectMocks
    private PautaService service;

    @Test
    void criar_happyPath_salvaPautaERetornaResponseComMesmoIdEDescricao() {
        PautaRequest request = new PautaRequest("Pauta teste");

        ArgumentCaptor<Pauta> captor = ArgumentCaptor.forClass(Pauta.class);

        PautaResponse response = service.criar(request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("Pauta teste", response.descricao());

        verify(repository).save(captor.capture());
        Pauta pauta = captor.getValue();
        assertNotNull(pauta);
        assertEquals(response.id(), pauta.getId());
        assertEquals("Pauta teste", pauta.getDescricao());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void criar_quandoRepositoryFalhar_propagaExcecao() {
        PautaRequest request = new PautaRequest("Erro DB");

        doThrow(new RuntimeException("db down")).when(repository).save(any(Pauta.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.criar(request));

        assertEquals("db down", ex.getMessage());

        verify(repository).save(any(Pauta.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void buscarEntidade_quandoExiste_retornaEntidade() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Pauta pauta = Pauta.criar(id, "Desc");

        when(repository.findById(id)).thenReturn(Optional.of(pauta));

        Pauta resultado = service.buscarEntidade(id);

        assertSame(pauta, resultado);

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void buscarEntidade_quandoNaoExiste_lancaNotFoundComMensagem() {
        UUID id = UUID.fromString("22222222-2222-2222-2222-222222222222");

        when(repository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.buscarEntidade(id));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Pauta não encontrada", ex.getReason());

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void buscar_happyPath_retornaResponseComIdEDescricao() {
        UUID id = UUID.fromString("33333333-3333-3333-3333-333333333333");
        Pauta pauta = Pauta.criar(id, "Pauta X");

        when(repository.findById(id)).thenReturn(Optional.of(pauta));

        PautaResponse response = service.buscar(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Pauta X", response.descricao());

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void buscar_quandoNaoExiste_lancaNotFoundComMensagem() {
        UUID id = UUID.fromString("44444444-4444-4444-4444-444444444444");

        when(repository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.buscar(id));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Pauta não encontrada", ex.getReason());

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }
}
