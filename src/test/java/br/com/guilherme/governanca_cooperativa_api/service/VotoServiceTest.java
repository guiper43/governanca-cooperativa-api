package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.client.CpfValidationClient;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Voto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.services.PautaService;
import br.com.guilherme.governanca_cooperativa_api.services.VotoService;
import br.com.guilherme.governanca_cooperativa_api.web.dto.client.CpfValidationResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoResponse;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {
    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PautaService pautaService;

    @Mock
    private CpfValidationClient client;

    @InjectMocks
    private VotoService votoService;

    @Test
    void votar_quandoSessaoNaoExiste_lancaNotFound() {
        UUID pautaId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        VotoRequest request = new VotoRequest("12345678901", VotoEscolha.SIM);

        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> votoService.votar(pautaId, request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Sessão não encontrada para a pauta", ex.getReason());

        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(sessaoRepository);
        verifyNoInteractions(client, pautaService, votoRepository);
    }

    @Test
    void votar_quandoSessaoEncerrada_lancaBusinessException() {
        UUID pautaId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        VotoRequest request = new VotoRequest("12345678901", VotoEscolha.SIM);

        Sessao sessao = mock(Sessao.class);
        when(sessao.getDataFechamento()).thenReturn(LocalDateTime.now().minusMinutes(1));

        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("Sessão encerrada", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(sessaoRepository);
        verifyNoInteractions(client, pautaService, votoRepository);
    }

    @Test
    void votar_quandoCpfStatusUnableToVote_lancaBusinessException() {
        UUID pautaId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        VotoRequest request = new VotoRequest("12345678901", VotoEscolha.SIM);

        Sessao sessao = mock(Sessao.class);
        when(sessao.getDataFechamento()).thenReturn(LocalDateTime.now().plusMinutes(5));

        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        CpfValidationResponse response = mock(CpfValidationResponse.class);
        when(response.status()).thenReturn(CpfValidationStatus.UNABLE_TO_VOTE);
        when(client.buscarStatusCpf(request.associadoId())).thenReturn(response);

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("CPF não está apto a votar", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(client).buscarStatusCpf("12345678901");
        verifyNoMoreInteractions(sessaoRepository, client);
        verifyNoInteractions(pautaService, votoRepository);
    }

    @Test
    void votar_quandoFeignNotFound_propagaFeignNotFound() {
        UUID pautaId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        VotoRequest request = new VotoRequest("12345678901", VotoEscolha.SIM);

        Sessao sessao = mock(Sessao.class);
        when(sessao.getDataFechamento()).thenReturn(LocalDateTime.now().plusMinutes(5));

        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        FeignException.NotFound feignNotFound = mock(FeignException.NotFound.class);
        when(client.buscarStatusCpf(request.associadoId())).thenThrow(feignNotFound);

        FeignException.NotFound ex = assertThrows(FeignException.NotFound.class, () -> votoService.votar(pautaId, request));
        assertSame(feignNotFound, ex);

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(client).buscarStatusCpf("12345678901");
        verifyNoMoreInteractions(sessaoRepository, client);
        verifyNoInteractions(pautaService, votoRepository);
    }

    @Test
    void votar_quandoFeignErroGenerico_lancaServiceUnavailable() {
        UUID pautaId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        VotoRequest request = new VotoRequest("12345678901", VotoEscolha.SIM);

        Sessao sessao = mock(Sessao.class);
        when(sessao.getDataFechamento()).thenReturn(LocalDateTime.now().plusMinutes(5));

        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        FeignException feignException = mock(FeignException.class);
        when(client.buscarStatusCpf(request.associadoId())).thenThrow(feignException);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> votoService.votar(pautaId, request));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
        assertEquals("Validação de CPF indisponível", ex.getReason());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(client).buscarStatusCpf("12345678901");
        verifyNoMoreInteractions(sessaoRepository, client);
        verifyNoInteractions(pautaService, votoRepository);
    }

    @Test
    void votar_quandoJaVotou_lancaBusinessException() {
        UUID pautaId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        VotoRequest request = new VotoRequest("12345678901", VotoEscolha.SIM);

        Sessao sessao = mock(Sessao.class);
        when(sessao.getDataFechamento()).thenReturn(LocalDateTime.now().plusMinutes(5));
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        CpfValidationResponse cpfResponse = mock(CpfValidationResponse.class);
        when(cpfResponse.status()).thenReturn(CpfValidationStatus.ABLE_TO_VOTE);
        when(client.buscarStatusCpf(request.associadoId())).thenReturn(cpfResponse);

        Pauta pauta = mock(Pauta.class);
        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        doThrow(new DataIntegrityViolationException("dup"))
            .when(votoRepository).save(any(Voto.class));

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("Associado já votou nessa sessão", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(client).buscarStatusCpf("12345678901");
        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).save(any(Voto.class));
        verifyNoMoreInteractions(sessaoRepository, client, pautaService, votoRepository);
    }


    @Test
    void votar_comDadosValidos_salvaVotoERetornaResponse() {
        UUID pautaId = UUID.fromString("77777777-7777-7777-7777-777777777777");
        VotoRequest request = new VotoRequest("12345678901", VotoEscolha.SIM);

        Sessao sessao = mock(Sessao.class);
        when(sessao.getDataFechamento()).thenReturn(LocalDateTime.now().plusMinutes(5));
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        CpfValidationResponse cpfResponse = mock(CpfValidationResponse.class);
        when(cpfResponse.status()).thenReturn(CpfValidationStatus.ABLE_TO_VOTE);
        when(client.buscarStatusCpf(request.associadoId())).thenReturn(cpfResponse);

        Pauta pauta = mock(Pauta.class);
        when(pauta.getId()).thenReturn(pautaId);
        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        ArgumentCaptor<Voto> captor = ArgumentCaptor.forClass(Voto.class);
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VotoResponse response = votoService.votar(pautaId, request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(pautaId, response.pautaId());
        assertEquals("12345678901", response.associadoId());
        assertEquals(VotoEscolha.SIM, response.votoEscolha());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(client).buscarStatusCpf("12345678901");
        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).save(captor.capture());

        Voto votoPersistido = captor.getValue();
        assertNotNull(votoPersistido);
        assertNotNull(votoPersistido.getId());
        assertSame(pauta, votoPersistido.getPauta());
        assertEquals("12345678901", votoPersistido.getAssociadoId());
        assertEquals(VotoEscolha.SIM, votoPersistido.getVotoEscolha());

        verifyNoMoreInteractions(sessaoRepository, client, pautaService, votoRepository);
    }

}
