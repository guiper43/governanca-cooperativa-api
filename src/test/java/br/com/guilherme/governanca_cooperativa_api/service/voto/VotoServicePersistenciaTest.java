package br.com.guilherme.governanca_cooperativa_api.service.voto;

import br.com.guilherme.governanca_cooperativa_api.service.gateway.CpfValidatorGateway;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Voto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.service.VotoService;
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
import static br.com.guilherme.governanca_cooperativa_api.utils.VotoServiceTestDataFactory.*;
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
    private CpfValidatorGateway cpfValidatorGateway;

    @InjectMocks
    private VotoService votoService;

    @Test
    void votar_quandoCpfApto_ePersistenciaOk_retornaVotoResponse() {
        UUID pautaId = uuid("88888888-8888-8888-8888-888888888888");
        UUID sessaoId = uuid("12121212-1212-1212-1212-121212121212");
        VotoInput request = new VotoInput(requestPadrao().associadoId(), requestPadrao().votoEscolha());

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        when(cpfValidatorGateway.validar(request.associadoId()))
                .thenReturn(CpfValidationStatus.ABLE_TO_VOTE);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        when(votoRepository.save(any(Voto.class))).thenAnswer(i -> i.getArguments()[0]);

        ArgumentCaptor<Voto> votoCaptor = ArgumentCaptor.forClass(Voto.class);

        VotoOutput response = votoService.votar(pautaId, request);

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(cpfValidatorGateway).validar(request.associadoId());
        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).save(votoCaptor.capture());

        Voto votoSalvo = votoCaptor.getValue();

        assertAll(
                () -> assertEquals(votoSalvo.getId(), response.id()),
                () -> assertEquals(pautaId, response.pautaId()),
                () -> assertEquals(CPF_VALIDO_MASCARADO, response.associadoId()),
                () -> assertEquals(request.votoEscolha(), response.votoEscolha()));

        verifyNoMoreInteractions(sessaoRepository, cpfValidatorGateway, pautaService, votoRepository);
    }

    @Test
    void votar_quandoCpfApto_eVotoDuplicado_lancaBusinessException() {
        UUID pautaId = uuid("99999999-9999-9999-9999-999999999999");
        UUID sessaoId = uuid("34343434-3434-3434-3434-343434343434");

        VotoInput requestBase = new VotoInput(requestPadrao().associadoId(), requestPadrao().votoEscolha());
        VotoInput request = new VotoInput(requestBase.associadoId(), VotoEscolha.NAO);

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        when(cpfValidatorGateway.validar(request.associadoId()))
                .thenReturn(CpfValidationStatus.ABLE_TO_VOTE);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);

        when(votoRepository.save(any(Voto.class))).thenThrow(new DataIntegrityViolationException("duplicado"));

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("Associado já votou nessa sessão", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(cpfValidatorGateway).validar(request.associadoId());
        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).save(any(Voto.class));

        verifyNoMoreInteractions(sessaoRepository, cpfValidatorGateway, pautaService, votoRepository);
    }
}
