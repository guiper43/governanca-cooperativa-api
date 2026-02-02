package br.com.guilherme.governanca_cooperativa_api.service.voto;

import br.com.guilherme.governanca_cooperativa_api.client.CpfValidationClient;
import br.com.guilherme.governanca_cooperativa_api.config.CpfValidationProperties;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Voto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.service.VotoService;
import br.com.guilherme.governanca_cooperativa_api.utils.validation.CpfLocalValidator;
import br.com.guilherme.governanca_cooperativa_api.web.dto.client.CpfValidationResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.*;
import static br.com.guilherme.governanca_cooperativa_api.utils.VotoServiceTestDataFactory.requestPadrao;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServicePersistenciaTest {

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
    void votar_quandoCpfApto_ePersistenciaOk_retornaVotoResponse() {
        UUID pautaId = uuid("88888888-8888-8888-8888-888888888888");
        UUID sessaoId = uuid("12121212-1212-1212-1212-121212121212");
        VotoRequest request = requestPadrao();

        when(properties.isEnabled()).thenReturn(true);

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        when(client.buscarStatusCpf(request.associadoId()))
            .thenReturn(new CpfValidationResponse(CpfValidationStatus.ABLE_TO_VOTE));

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        ArgumentCaptor<Voto> votoCaptor = ArgumentCaptor.forClass(Voto.class);

        VotoResponse response = votoService.votar(pautaId, request);

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(properties).isEnabled();
        verify(client).buscarStatusCpf(request.associadoId());
        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).save(votoCaptor.capture());

        Voto votoSalvo = votoCaptor.getValue();

        assertAll(
            () -> assertEquals(votoSalvo.getId(), response.id()),
            () -> assertEquals(pautaId, response.pautaId()),
            () -> assertEquals(request.associadoId(), response.associadoId()),
            () -> assertEquals(request.votoEscolha(), response.votoEscolha())
        );

        verifyNoInteractions(validator);
        verifyNoMoreInteractions(sessaoRepository, properties, client, pautaService, votoRepository);
    }

    @Test
    void votar_quandoCpfApto_eVotoDuplicado_lancaBusinessException() {
        UUID pautaId = uuid("99999999-9999-9999-9999-999999999999");
        UUID sessaoId = uuid("34343434-3434-3434-3434-343434343434");

        VotoRequest requestBase = requestPadrao();
        VotoRequest request = new VotoRequest(requestBase.associadoId(), VotoEscolha.NAO);

        when(properties.isEnabled()).thenReturn(true);

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        when(client.buscarStatusCpf(request.associadoId()))
            .thenReturn(new CpfValidationResponse(CpfValidationStatus.ABLE_TO_VOTE));

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        when(votoRepository.save(any(Voto.class))).thenThrow(new DataIntegrityViolationException("duplicado"));

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("Associado já votou nessa sessão", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(properties).isEnabled();
        verify(client).buscarStatusCpf(request.associadoId());
        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).save(any(Voto.class));

        verifyNoInteractions(validator);
        verifyNoMoreInteractions(sessaoRepository, properties, client, pautaService, votoRepository);
    }
}
