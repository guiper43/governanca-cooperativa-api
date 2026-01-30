package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.services.PautaService;
import br.com.guilherme.governanca_cooperativa_api.services.SessaoService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {
    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private SessaoService sessaoService;

    @Test
    void abrir_comDadosValidos_salvaSessaoERetornaResponse() {
        UUID pautaId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        SessaoRequest request = new SessaoRequest(10);

        Pauta pauta = mock(Pauta.class);
        when(pauta.getId()).thenReturn(pautaId);

        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());
        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        ArgumentCaptor<Sessao> captor = ArgumentCaptor.forClass(Sessao.class);

        SessaoResponse response = sessaoService.abrir(pautaId, request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(pautaId, response.pautaId());
        assertNotNull(response.dataAbertura());
        assertNotNull(response.dataFechamento());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(pautaService).buscarEntidade(pautaId);
        verify(sessaoRepository).save(captor.capture());

        Sessao sessaoPersistida = captor.getValue();
        assertNotNull(sessaoPersistida);
        assertNotNull(sessaoPersistida.getId());
        assertSame(pauta, sessaoPersistida.getPauta());

        long minutos = ChronoUnit.MINUTES.between(
            sessaoPersistida.getDataAbertura(),
            sessaoPersistida.getDataFechamento()
        );
        assertEquals(10, minutos);

        assertEquals(sessaoPersistida.getDataAbertura(), response.dataAbertura());
        assertEquals(sessaoPersistida.getDataFechamento(), response.dataFechamento());

        verifyNoMoreInteractions(sessaoRepository, pautaService);
    }

    @Test
    void abrir_comDuracaoNull_usaDefaultUmMinutoESalva() {
        UUID pautaId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        SessaoRequest request = new SessaoRequest(null);

        Pauta pauta = mock(Pauta.class);
        when(pauta.getId()).thenReturn(pautaId);

        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());
        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        ArgumentCaptor<Sessao> captor = ArgumentCaptor.forClass(Sessao.class);

        SessaoResponse response = sessaoService.abrir(pautaId, request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(pautaId, response.pautaId());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(pautaService).buscarEntidade(pautaId);
        verify(sessaoRepository).save(captor.capture());

        Sessao sessaoPersistida = captor.getValue();

        long minutos = ChronoUnit.MINUTES.between(
            sessaoPersistida.getDataAbertura(),
            sessaoPersistida.getDataFechamento()
        );
        assertEquals(1, minutos);

        assertEquals(sessaoPersistida.getDataAbertura(), response.dataAbertura());
        assertEquals(sessaoPersistida.getDataFechamento(), response.dataFechamento());

        verifyNoMoreInteractions(sessaoRepository, pautaService);
    }

    @Test
    void abrir_comDuracaoZero_lancaBusinessExceptionENaoSalva() {
        UUID pautaId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        SessaoRequest request = new SessaoRequest(0);

        BusinessException ex = assertThrows(
            BusinessException.class,
            () -> sessaoService.abrir(pautaId, request)
        );

        assertEquals("Duração inválida", ex.getMessage());

        verifyNoInteractions(sessaoRepository, pautaService);
    }

    @Test
    void abrir_comDuracaoNegativa_lancaBusinessExceptionENaoSalva() {
        UUID pautaId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        SessaoRequest request = new SessaoRequest(-5);

        BusinessException ex = assertThrows(
            BusinessException.class,
            () -> sessaoService.abrir(pautaId, request)
        );

        assertEquals("Duração inválida", ex.getMessage());

        verifyNoInteractions(sessaoRepository, pautaService);
    }

    @Test
    void abrir_quandoSessaoJaExiste_lancaConflictENaoBuscaPautaNemSalva() {
        UUID pautaId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        SessaoRequest request = new SessaoRequest(10);

        when(sessaoRepository.findByPautaId(pautaId))
            .thenReturn(Optional.of(mock(Sessao.class)));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> sessaoService.abrir(pautaId, request)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Sessão já existe para a pauta", ex.getReason());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(sessaoRepository, never()).save(any(Sessao.class));
        verifyNoMoreInteractions(sessaoRepository);
        verifyNoInteractions(pautaService);
    }

    @Test
    void abrir_quandoPautaNaoExiste_propagaNotFoundENaoSalva() {
        UUID pautaId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        SessaoRequest request = new SessaoRequest(10);

        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());
        when(pautaService.buscarEntidade(pautaId))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> sessaoService.abrir(pautaId, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Pauta não encontrada", ex.getReason());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(pautaService).buscarEntidade(pautaId);
        verify(sessaoRepository, never()).save(any(Sessao.class));
        verifyNoMoreInteractions(sessaoRepository, pautaService);
    }
}