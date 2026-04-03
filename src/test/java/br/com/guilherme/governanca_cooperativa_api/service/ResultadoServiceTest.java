package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.ResultadoOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.ResultadoStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.UrnaVotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.pautaPadrao;
import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.sessaoAberta;
import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.sessaoEncerrada;
import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.uuid;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private UrnaVotoRepository urnaVotoRepository;

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
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta nao encontrada"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.consultar(pautaId));

        verify(pautaService).buscarEntidade(pautaId);
        verifyNoMoreInteractions(pautaService);
        verifyNoInteractions(urnaVotoRepository, sessaoRepository);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode()),
                () -> assertEquals("Pauta nao encontrada", ex.getReason()));
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
        verifyNoInteractions(urnaVotoRepository);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode()),
                () -> assertEquals("Sessao nao encontrada para a pauta", ex.getReason()));
    }

    @Test
    void consultar_sessaoEmAndamento_retornaEmAndamentoSemParcial() {
        UUID pautaId = uuid("33333333-3333-3333-3333-333333333333");
        UUID sessaoId = uuid("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        Pauta pauta = pautaPadrao(pautaId);
        Sessao sessao = sessaoAberta(pauta, sessaoId);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        ResultadoOutput response = service.consultar(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(pautaService, sessaoRepository);
        verifyNoInteractions(urnaVotoRepository);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(pautaId, response.pautaId()),
                () -> assertNull(response.votosSim()),
                () -> assertNull(response.votosNao()),
                () -> assertEquals(ResultadoStatus.EM_ANDAMENTO, response.status()));
    }

    @Test
    void consultar_sessaoEncerrada_totalSimMaior_retornaAprovada() {
        UUID pautaId = uuid("44444444-4444-4444-4444-444444444444");
        UUID sessaoId = uuid("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        Pauta pauta = pautaPadrao(pautaId);
        Sessao sessao = sessaoEncerrada(pauta, sessaoId);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(urnaVotoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM)).thenReturn(5L);
        when(urnaVotoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO)).thenReturn(1L);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        ResultadoOutput response = service.consultar(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(urnaVotoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM);
        verify(urnaVotoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO);
        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(pautaService, urnaVotoRepository, sessaoRepository);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(pautaId, response.pautaId()),
                () -> assertEquals(5L, response.votosSim()),
                () -> assertEquals(1L, response.votosNao()),
                () -> assertEquals(ResultadoStatus.APROVADA, response.status()));
    }

    @Test
    void consultar_sessaoEncerrada_totalNaoMaior_retornaReprovada() {
        UUID pautaId = uuid("55555555-5555-5555-5555-555555555555");
        UUID sessaoId = uuid("cccccccc-cccc-cccc-cccc-cccccccccccc");
        Pauta pauta = pautaPadrao(pautaId);
        Sessao sessao = sessaoEncerrada(pauta, sessaoId);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(urnaVotoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM)).thenReturn(2L);
        when(urnaVotoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO)).thenReturn(7L);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        ResultadoOutput response = service.consultar(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(urnaVotoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM);
        verify(urnaVotoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO);
        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(pautaService, urnaVotoRepository, sessaoRepository);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(pautaId, response.pautaId()),
                () -> assertEquals(2L, response.votosSim()),
                () -> assertEquals(7L, response.votosNao()),
                () -> assertEquals(ResultadoStatus.REPROVADA, response.status()));
    }

    @Test
    void consultar_sessaoEncerrada_totaisIguais_retornaEmpate() {
        UUID pautaId = uuid("66666666-6666-6666-6666-666666666666");
        UUID sessaoId = uuid("dddddddd-dddd-dddd-dddd-dddddddddddd");
        Pauta pauta = pautaPadrao(pautaId);
        Sessao sessao = sessaoEncerrada(pauta, sessaoId);

        when(pautaService.buscarEntidade(pautaId)).thenReturn(pauta);
        when(urnaVotoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM)).thenReturn(3L);
        when(urnaVotoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO)).thenReturn(3L);
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.of(sessao));

        ResultadoOutput response = service.consultar(pautaId);

        verify(pautaService).buscarEntidade(pautaId);
        verify(urnaVotoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM);
        verify(urnaVotoRepository).countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO);
        verify(sessaoRepository).findByPautaId(pautaId);
        verifyNoMoreInteractions(pautaService, urnaVotoRepository, sessaoRepository);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(pautaId, response.pautaId()),
                () -> assertEquals(3L, response.votosSim()),
                () -> assertEquals(3L, response.votosNao()),
                () -> assertEquals(ResultadoStatus.EMPATE, response.status()));
    }
}
