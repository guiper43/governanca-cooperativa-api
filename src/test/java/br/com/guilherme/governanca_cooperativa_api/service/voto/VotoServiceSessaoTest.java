package br.com.guilherme.governanca_cooperativa_api.service.voto;

import br.com.guilherme.governanca_cooperativa_api.client.CpfValidationClient;
import br.com.guilherme.governanca_cooperativa_api.config.CpfValidationProperties;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.service.VotoService;
import br.com.guilherme.governanca_cooperativa_api.utils.validation.CpfLocalValidator;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class VotoServiceSessaoTest {

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
    void votar_quandoSessaoNaoEncontrada_lancaResponseStatusNotFound() {
        UUID pautaId = uuid("11111111-1111-1111-1111-111111111111");
        VotoRequest request = requestPadrao();

        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> votoService.votar(pautaId, request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Sessão não encontrada para a pauta", ex.getReason());

        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(sessaoRepository);
        verifyNoInteractions(votoRepository, pautaService, client, validator, properties);
    }

    @Test
    void votar_quandoSessaoEncerrada_lancaBusinessException() {
        UUID pautaId = uuid("22222222-2222-2222-2222-222222222222");
        UUID sessaoId = uuid("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        VotoRequest request = requestPadrao();

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoEncerrada(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("Sessão encerrada", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(sessaoRepository);
        verifyNoInteractions(votoRepository, pautaService, client, validator, properties);
    }
}
