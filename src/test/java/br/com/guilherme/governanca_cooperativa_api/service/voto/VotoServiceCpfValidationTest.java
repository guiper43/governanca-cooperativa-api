package br.com.guilherme.governanca_cooperativa_api.service.voto;

import br.com.guilherme.governanca_cooperativa_api.client.CpfValidationClient;
import br.com.guilherme.governanca_cooperativa_api.config.CpfValidationProperties;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.services.PautaService;
import br.com.guilherme.governanca_cooperativa_api.services.VotoService;
import br.com.guilherme.governanca_cooperativa_api.utils.validation.CpfLocalValidator;
import br.com.guilherme.governanca_cooperativa_api.web.dto.client.CpfValidationResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoRequest;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.*;
import static br.com.guilherme.governanca_cooperativa_api.utils.VotoServiceTestDataFactory.requestPadrao;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceCpfValidationTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PautaService pautaService;

    @Mock
    private CpfValidationClient client;

    @Mock
    private CpfLocalValidator validator;

    @Mock
    private CpfValidationProperties properties;

    @InjectMocks
    private VotoService votoService;

    @Test
    void votar_quandoIntegracaoHabilitada_eStatusUnableToVote_lancaBusinessException() {
        UUID pautaId = uuid("33333333-3333-3333-3333-333333333333");
        UUID sessaoId = uuid("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        VotoRequest request = requestPadrao();

        when(properties.isEnabled()).thenReturn(true);

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        when(client.buscarStatusCpf(request.associadoId()))
            .thenReturn(new CpfValidationResponse(CpfValidationStatus.UNABLE_TO_VOTE));

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("CPF não está apto a votar", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(properties).isEnabled();
        verify(client).buscarStatusCpf(request.associadoId());

        verifyNoInteractions(validator, pautaService, votoRepository);
        verifyNoMoreInteractions(sessaoRepository, properties, client);
    }

    @ParameterizedTest
    @CsvSource({
        "404, NOT_FOUND, CPF inválido",
        "400, SERVICE_UNAVAILABLE, Validação de CPF indisponível",
        "429, SERVICE_UNAVAILABLE, Validação de CPF indisponível",
        "500, SERVICE_UNAVAILABLE, Validação de CPF indisponível",
        "503, SERVICE_UNAVAILABLE, Validação de CPF indisponível"
    })
    void votar_quandoFeignException_eFallbackDesligado_retornaErroAdequado(int httpStatus, HttpStatus expected, String reason) {
        UUID pautaId = uuid("44444444-4444-4444-4444-444444444444");
        UUID sessaoId = uuid("cccccccc-cccc-cccc-cccc-cccccccccccc");
        VotoRequest request = requestPadrao();

        when(properties.isEnabled()).thenReturn(true);
        when(properties.isFallbackEnabled()).thenReturn(false);

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(httpStatus);
        when(client.buscarStatusCpf(request.associadoId())).thenThrow(feignException);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> votoService.votar(pautaId, request));

        assertEquals(expected, ex.getStatusCode());
        assertEquals(reason, ex.getReason());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(properties).isEnabled();
        verify(properties).isFallbackEnabled();
        verify(client).buscarStatusCpf(request.associadoId());

        verifyNoInteractions(validator, pautaService, votoRepository);
        verifyNoMoreInteractions(sessaoRepository, properties, client);
    }

    @Test
    void votar_quandoFeignErro_eFallbackLigado_usaValidacaoLocal() {
        UUID pautaId = uuid("55555555-5555-5555-5555-555555555555");
        UUID sessaoId = uuid("dddddddd-dddd-dddd-dddd-dddddddddddd");
        VotoRequest request = requestPadrao();

        when(properties.isEnabled()).thenReturn(true);
        when(properties.isFallbackEnabled()).thenReturn(true);

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(503);
        when(client.buscarStatusCpf(request.associadoId())).thenThrow(feignException);

        when(validator.validarStatus(request.associadoId())).thenReturn(CpfValidationStatus.UNABLE_TO_VOTE);

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("CPF não está apto a votar", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(properties).isEnabled();
        verify(properties).isFallbackEnabled();
        verify(client).buscarStatusCpf(request.associadoId());
        verify(validator).validarStatus(request.associadoId());

        verifyNoInteractions(pautaService, votoRepository);
        verifyNoMoreInteractions(sessaoRepository, properties, client, validator);
    }

    @Test
    void votar_quandoFeign404_eFallbackLigado_usaValidacaoLocal() {
        UUID pautaId = uuid("57575757-5757-5757-5757-575757575757");
        UUID sessaoId = uuid("abababab-abab-abab-abab-abababababab");
        VotoRequest request = requestPadrao();

        when(properties.isEnabled()).thenReturn(true);
        when(properties.isFallbackEnabled()).thenReturn(true);

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(404);
        when(client.buscarStatusCpf(request.associadoId())).thenThrow(feignException);

        when(validator.validarStatus(request.associadoId())).thenReturn(CpfValidationStatus.UNABLE_TO_VOTE);

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("CPF não está apto a votar", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(properties).isEnabled();
        verify(properties).isFallbackEnabled();
        verify(client).buscarStatusCpf(request.associadoId());
        verify(validator).validarStatus(request.associadoId());

        verifyNoInteractions(pautaService, votoRepository);
        verifyNoMoreInteractions(sessaoRepository, properties, client, validator);
    }

    @Test
    void votar_quandoIntegracaoDesabilitada_usaValidacaoLocal_eNaoChamaClient() {
        UUID pautaId = uuid("66666666-6666-6666-6666-666666666666");
        UUID sessaoId = uuid("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
        VotoRequest request = requestPadrao();

        when(properties.isEnabled()).thenReturn(false);

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        when(validator.validarStatus(request.associadoId())).thenReturn(CpfValidationStatus.UNABLE_TO_VOTE);

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("CPF não está apto a votar", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(properties).isEnabled();
        verify(validator).validarStatus(request.associadoId());

        verifyNoInteractions(client, pautaService, votoRepository);
        verifyNoMoreInteractions(sessaoRepository, properties, validator);
    }
}
