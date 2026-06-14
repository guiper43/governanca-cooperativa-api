package br.com.guilherme.governanca_cooperativa_api.service.voto;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.RegistroParticipacao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.UrnaVoto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.ParticipacaoStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.RegistroParticipacaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.UrnaVotoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.service.VotoService;
import br.com.guilherme.governanca_cooperativa_api.service.gateway.CpfValidatorGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.pautaPadrao;
import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.sessaoAberta;
import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.uuid;
import static br.com.guilherme.governanca_cooperativa_api.utils.VotoServiceTestDataFactory.requestPadrao;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VotoServicePersistenciaTest {

    @Mock
    private RegistroParticipacaoRepository registroParticipacaoRepository;

    @Mock
    private UrnaVotoRepository urnaVotoRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PautaService pautaService;

    @Mock
    private CpfValidatorGateway cpfValidatorGateway;

    @InjectMocks
    private VotoService votoService;

    @Test
    void votar_quandoCpfApto_ePersistenciaOk_retornaConfirmacaoSemExporEscolha() {
        UUID pautaId = uuid("88888888-8888-8888-8888-888888888888");
        UUID sessaoId = uuid("12121212-1212-1212-1212-121212121212");
        VotoInput request = new VotoInput(requestPadrao().associadoId(), requestPadrao().votoEscolha());

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(cpfValidatorGateway.validar(request.associadoId())).thenReturn(CpfValidationStatus.ABLE_TO_VOTE);
        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(registroParticipacaoRepository.existsByPautaIdAndAssociadoId(pautaId, request.associadoId()))
                .thenReturn(false);
        when(registroParticipacaoRepository.save(any(RegistroParticipacao.class))).thenAnswer(i -> i.getArguments()[0]);
        when(urnaVotoRepository.save(any(UrnaVoto.class))).thenAnswer(i -> i.getArguments()[0]);

        ArgumentCaptor<RegistroParticipacao> registroCaptor = ArgumentCaptor.forClass(RegistroParticipacao.class);
        ArgumentCaptor<UrnaVoto> urnaCaptor = ArgumentCaptor.forClass(UrnaVoto.class);

        VotoOutput response = votoService.votar(pautaId, request);

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(cpfValidatorGateway).validar(request.associadoId());
        verify(pautaService).buscarEntidade(pautaId);
        verify(registroParticipacaoRepository).existsByPautaIdAndAssociadoId(pautaId, request.associadoId());
        verify(registroParticipacaoRepository, org.mockito.Mockito.times(1)).save(registroCaptor.capture());
        verify(urnaVotoRepository).save(urnaCaptor.capture());

        RegistroParticipacao ultimoRegistroSalvo = registroCaptor.getValue();
        UrnaVoto votoDepositado = urnaCaptor.getValue();

        assertAll(
                () -> assertEquals(ultimoRegistroSalvo.getProtocoloPublico(), response.protocolo()),
                () -> assertEquals(pautaId, response.pautaId()),
                () -> assertEquals("REGISTRADO", response.status()),
                () -> assertEquals(ParticipacaoStatus.CONSUMIDO, ultimoRegistroSalvo.getStatus()),
                () -> assertEquals(request.associadoId(), ultimoRegistroSalvo.getAssociadoId()),
                () -> assertNotNull(ultimoRegistroSalvo.getTokenHash()),
                () -> assertEquals(pautaId, votoDepositado.getPauta().getId()),
                () -> assertEquals(request.votoEscolha(), votoDepositado.getVotoEscolha()));

        verifyNoMoreInteractions(sessaoRepository, cpfValidatorGateway, pautaService,
                registroParticipacaoRepository, urnaVotoRepository);
    }

    @Test
    void votar_quandoAssociadoJaParticipou_lancaBusinessException() {
        UUID pautaId = uuid("99999999-9999-9999-9999-999999999999");
        UUID sessaoId = uuid("34343434-3434-3434-3434-343434343434");

        VotoInput requestBase = new VotoInput(requestPadrao().associadoId(), requestPadrao().votoEscolha());
        VotoInput request = new VotoInput(requestBase.associadoId(), VotoEscolha.NAO);

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, sessaoId);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));
        when(cpfValidatorGateway.validar(request.associadoId())).thenReturn(CpfValidationStatus.ABLE_TO_VOTE);
        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(registroParticipacaoRepository.existsByPautaIdAndAssociadoId(pautaId, request.associadoId()))
                .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("Associado ja votou nessa sessao", ex.getMessage());

        verify(sessaoRepository).findByPautaId(pautaId);
        verify(cpfValidatorGateway).validar(request.associadoId());
        verify(pautaService).buscarEntidade(pautaId);
        verify(registroParticipacaoRepository).existsByPautaIdAndAssociadoId(pautaId, request.associadoId());
        verifyNoMoreInteractions(sessaoRepository, cpfValidatorGateway, pautaService,
                registroParticipacaoRepository, urnaVotoRepository);
    }
}
