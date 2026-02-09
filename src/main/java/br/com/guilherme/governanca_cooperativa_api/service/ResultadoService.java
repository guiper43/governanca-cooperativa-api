package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.dto.ResultadoOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.ResultadoStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultadoService {
    private final VotoRepository votoRepository;
    private final SessaoRepository sessaoRepository;
    private final PautaService pautaService;

    public ResultadoOutput consultar(UUID pautaId) {
        log.info("Iniciando consulta de resultado. pautaId={}", pautaId);
        pautaService.buscarEntidade(pautaId);

        Sessao sessao = buscarSessao(pautaId);
        long totalSim = contarVotos(pautaId, VotoEscolha.SIM);
        long totalNao = contarVotos(pautaId, VotoEscolha.NAO);
        ResultadoStatus status = calcularStatus(sessao, totalSim, totalNao);

        log.info("Resultado calculado. pautaId={} status={} sim={} nao={}", pautaId, status, totalSim, totalNao);
        return new ResultadoOutput(pautaId, totalSim, totalNao, status);
    }

    private Sessao buscarSessao(UUID pautaId) {
        return sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> {
                    log.warn("Sessão não encontrada para pauta. pautaId={}", pautaId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada para a pauta");
                });
    }

    private long contarVotos(UUID pautaId, VotoEscolha escolha) {
        return votoRepository.countByPautaIdAndVotoEscolha(pautaId, escolha);
    }

    private ResultadoStatus calcularStatus(Sessao sessao, long totalSim, long totalNao) {
        if (LocalDateTime.now().isBefore(sessao.getDataFechamento())) {
            return ResultadoStatus.EM_ANDAMENTO;
        }
        if (totalSim > totalNao) {
            return ResultadoStatus.APROVADA;
        }
        if (totalNao > totalSim) {
            return ResultadoStatus.REPROVADA;
        }
        return ResultadoStatus.EMPATE;
    }
}
