package br.com.guilherme.governanca_cooperativa_api.service.voto;

import br.com.guilherme.governanca_cooperativa_api.service.gateway.CpfValidatorGateway;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.CpfValidationStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.service.VotoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private SessaoRepository sessaoRepository;
    @Mock
    private PautaService pautaService;
    @Mock
    private CpfValidatorGateway cpfValidatorGateway;
    @InjectMocks
    private VotoService votoService;

    @Test
    void votar_quandoCpfInapto_lancaBusinessException() {
        UUID pautaId = uuid("33333333-3333-3333-3333-333333333333");
        VotoInput request = requestPadrao();

        var pauta = pautaPadrao(pautaId);
        var sessao = sessaoAberta(pauta, UUID.randomUUID());
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        when(cpfValidatorGateway.validar(request.associadoId()))
                .thenReturn(CpfValidationStatus.UNABLE_TO_VOTE);

        BusinessException ex = assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        assertEquals("CPF não está apto a votar", ex.getMessage());

        verify(cpfValidatorGateway).validar(request.associadoId());
    }
}
