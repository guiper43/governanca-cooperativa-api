package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.ResultadoStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.resultado.ResultadoResponse;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private ResultadoService service;

    @Test
    void consultar_pautaNaoExiste_propagaNotFoundENaoInterageComRepos() {
        UUID pautaId = uuid("11111111-1111-1111-1111-111111111111");

        when(pautaService.buscarEntidade(pautaId))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.consultar(pautaId));

        verify(pautaService).buscarEntidade(pautaId);
        verifyNoMoreInteractions(pautaService);
        verifyNoInteractions(votoRepository, sessaoRepository);

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode()),
            () -> assertEquals("Pauta não encontrada", ex.getReason())
        );
    }

    @Test
    void consultar_sessaoNaoExiste_lancaNotFound() {
        UUID pautaId = uuid("22222222-2222-2222-2222-222222222222");
        Pauta pauta = pautaPadrao(pautaId);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.consultar(pautaId));

        verify(pautaService).buscarEntidade(pautaId);
        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(pautaService, sessaoRepository);
        verifyNoInteractions(votoRepository);

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode()),
            () -> assertEquals("Sessão não encontrada para a pauta", ex.getReason()));
    }


    @Test
    void consultar_sessaoEmAndamento_retornaEmAndamento() {
        UUID pautaId = uuid("33333333-3333-3333-3333-333333333333");
        UUID sessaoId = uuid("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        Pauta pauta = pautaPadrao(pautaId);
        Sessao sessao = sessaoAberta(pauta, sessaoId);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM)).thenReturn(10L);
        when(votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO)).thenReturn(2L);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        ResultadoResponse response = service.consultar(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM);
        verify(votoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO);
        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(pautaService, votoRepository, sessaoRepository);

        assertAll(
            () -> assertNotNull(response),
            () -> assertEquals(pautaId, response.pautaId()),
            () -> assertEquals(10L, response.totalSim()),
            () -> assertEquals(2L, response.totalNao()),
            () -> assertEquals(ResultadoStatus.EM_ANDAMENTO, response.status())
        );
    }

    @Test
    void consultar_sessaoEncerrada_totalSimMaior_retornaAprovada() {
        UUID pautaId = uuid("44444444-4444-4444-4444-444444444444");
        UUID sessaoId = uuid("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        Pauta pauta = pautaPadrao(pautaId);
        Sessao sessao = sessaoEncerrada(pauta, sessaoId);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM)).thenReturn(5L);
        when(votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO)).thenReturn(1L);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        ResultadoResponse response = service.consultar(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM);
        verify(votoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO);
        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(pautaService, votoRepository, sessaoRepository);

        assertAll(
            () -> assertNotNull(response),
            () -> assertEquals(pautaId, response.pautaId()),
            () -> assertEquals(5L, response.totalSim()),
            () -> assertEquals(1L, response.totalNao()),
            () -> assertEquals(ResultadoStatus.APROVADA, response.status())
        );
    }

    @Test
    void consultar_sessaoEncerrada_totalNaoMaior_retornaReprovada() {
        UUID pautaId = uuid("55555555-5555-5555-5555-555555555555");
        UUID sessaoId = uuid("cccccccc-cccc-cccc-cccc-cccccccccccc");
        Pauta pauta = pautaPadrao(pautaId);
        Sessao sessao = sessaoEncerrada(pauta, sessaoId);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM)).thenReturn(2L);
        when(votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO)).thenReturn(7L);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        ResultadoResponse response = service.consultar(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM);
        verify(votoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO);
        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(pautaService, votoRepository, sessaoRepository);

        assertAll(
            () -> assertNotNull(response),
            () -> assertEquals(pautaId, response.pautaId()),
            () -> assertEquals(2L, response.totalSim()),
            () -> assertEquals(7L, response.totalNao()),
            () -> assertEquals(ResultadoStatus.REPROVADA, response.status())
        );
    }

    @Test
    void consultar_sessaoEncerrada_totaisIguais_retornaEmpate() {
        UUID pautaId = uuid("66666666-6666-6666-6666-666666666666");
        UUID sessaoId = uuid("dddddddd-dddd-dddd-dddd-dddddddddddd");
        Pauta pauta = pautaPadrao(pautaId);
        Sessao sessao = sessaoEncerrada(pauta, sessaoId);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM)).thenReturn(3L);
        when(votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO)).thenReturn(3L);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        ResultadoResponse response = service.consultar(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(votoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM);
        verify(votoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO);
        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(pautaService, votoRepository, sessaoRepository);

        assertAll(
            () -> assertNotNull(response),
            () -> assertEquals(pautaId, response.pautaId()),
            () -> assertEquals(3L, response.totalSim()),
            () -> assertEquals(3L, response.totalNao()),
            () -> assertEquals(ResultadoStatus.EMPATE, response.status())
        );
    }
}