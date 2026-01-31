package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.web.dto.sessao.SessaoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.sessao.SessaoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.pautaPadrao;
import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.uuid;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    @Mock
    private SessaoRepository repository;

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private SessaoService service;

    @Test
    void abrir_dadosValidos_salvaSessaoERetornaResponse() {
        UUID pautaId = uuid("11111111-1111-1111-1111-111111111111");
        SessaoRequest request = new SessaoRequest(10);
        Pauta pauta = pautaPadrao(pautaId);
        ArgumentCaptor<Sessao> captor = ArgumentCaptor.forClass(Sessao.class);

        when(repository.findByPautaId(pautaId)).thenReturn(Optional.empty());
        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        SessaoResponse response = service.abrir(pautaId, request);

        verify(repository).findByPautaId(pautaId);
        verify(pautaService).buscarEntidade(pautaId);
        verify(repository).save(captor.capture());
        verifyNoMoreInteractions(repository, pautaService);

        Sessao sessao = captor.getValue();
        long minutos = ChronoUnit.MINUTES.between(sessao.getDataAbertura(), sessao.getDataFechamento());

        assertAll(
            () -> assertNotNull(response),
            () -> assertNotNull(response.id()),
            () -> assertEquals(pautaId, response.pautaId()),
            () -> assertNotNull(response.dataAbertura()),
            () -> assertNotNull(response.dataFechamento()),
            () -> assertNotNull(sessao),
            () -> assertNotNull(sessao.getId()),
            () -> assertSame(pauta, sessao.getPauta()),
            () -> assertEquals(10, minutos),
            () -> assertEquals(sessao.getId(), response.id()),
            () -> assertEquals(sessao.getDataAbertura(), response.dataAbertura()),
            () -> assertEquals(sessao.getDataFechamento(), response.dataFechamento())
        );
    }

    @Test
    void abrir_duracaoNull_usaDefaultUmMinutoESalvaSessao() {
        UUID pautaId = uuid("22222222-2222-2222-2222-222222222222");
        SessaoRequest request = new SessaoRequest(null);
        Pauta pauta = pautaPadrao(pautaId);
        ArgumentCaptor<Sessao> captor = ArgumentCaptor.forClass(Sessao.class);

        when(repository.findByPautaId(pautaId)).thenReturn(Optional.empty());
        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        SessaoResponse response = service.abrir(pautaId, request);

        verify(repository).findByPautaId(pautaId);
        verify(pautaService).buscarEntidade(pautaId);
        verify(repository).save(captor.capture());
        verifyNoMoreInteractions(repository, pautaService);

        Sessao sessao = captor.getValue();
        long minutos = ChronoUnit.MINUTES.between(sessao.getDataAbertura(), sessao.getDataFechamento());

        assertAll(
            () -> assertNotNull(response),
            () -> assertNotNull(response.id()),
            () -> assertEquals(pautaId, response.pautaId()),
            () -> assertEquals(1, minutos),
            () -> assertEquals(sessao.getId(), response.id()),
            () -> assertEquals(sessao.getDataAbertura(), response.dataAbertura()),
            () -> assertEquals(sessao.getDataFechamento(), response.dataFechamento())
        );
    }

    @Test
    void abrir_duracaoZero_lancaBusinessExceptionENaoInterage() {
        UUID pautaId = uuid("33333333-3333-3333-3333-333333333333");
        SessaoRequest request = new SessaoRequest(0);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.abrir(pautaId, request));

        assertEquals("Duração inválida", ex.getMessage());
        verifyNoInteractions(repository, pautaService);
    }

    @Test
    void abrir_duracaoNegativa_lancaBusinessExceptionENaoInterage() {
        UUID pautaId = uuid("44444444-4444-4444-4444-444444444444");
        SessaoRequest request = new SessaoRequest(-5);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.abrir(pautaId, request));

        assertEquals("Duração inválida", ex.getMessage());
        verifyNoInteractions(repository, pautaService);
    }

    @Test
    void abrir_sessaoJaExiste_lancaConflictENaoBuscaPautaNemSalva() {
        UUID pautaId = uuid("55555555-5555-5555-5555-555555555555");
        SessaoRequest request = new SessaoRequest(10);

        when(repository.findByPautaId(pautaId)).thenReturn(Optional.of(mock(Sessao.class)));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.abrir(pautaId, request));

        verify(repository).findByPautaId(pautaId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(pautaService);

        assertAll(
            () -> assertEquals(HttpStatus.CONFLICT, ex.getStatusCode()),
            () -> assertEquals("Sessão já existe para a pauta", ex.getReason())
        );
    }

    @Test
    void abrir_pautaNaoExiste_propagaNotFoundENaoSalva() {
        UUID pautaId = uuid("66666666-6666-6666-6666-666666666666");
        SessaoRequest request = new SessaoRequest(10);

        when(repository.findByPautaId(pautaId)).thenReturn(Optional.empty());
        when(pautaService.buscarEntidade(pautaId))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.abrir(pautaId, request));

        verify(repository).findByPautaId(pautaId);
        verify(pautaService).buscarEntidade(pautaId);
        verify(repository, never()).save(any(Sessao.class));
        verifyNoMoreInteractions(repository, pautaService);

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode()),
            () -> assertEquals("Pauta não encontrada", ex.getReason())
        );
    }

}