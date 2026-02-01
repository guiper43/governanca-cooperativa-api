package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.ResultadoStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.web.dto.resultado.ResultadoResponse;
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

    public ResultadoResponse consultar(UUID pautaId) {
        log.info("Iniciando consulta de resultado. pautaId={}", pautaId);
        pautaService.buscarEntidade(pautaId);

        Sessao sessao = sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> {
                    log.warn("Sessão não encontrada para pauta. pautaId={}", pautaId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada para a pauta");
                });

        long totalSim = votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.SIM);
        long totalNao = votoRepository.countByPautaIdAndVotoEscolha(pautaId, VotoEscolha.NAO);

        LocalDateTime agora = LocalDateTime.now();

        ResultadoStatus status;
        if (agora.isBefore(sessao.getDataFechamento())) {
            status = ResultadoStatus.EM_ANDAMENTO;
        } else if (totalSim > totalNao) {
            status = ResultadoStatus.APROVADA;
        } else if (totalNao > totalSim) {
            status = ResultadoStatus.REPROVADA;
        } else {
            status = ResultadoStatus.EMPATE;
        }

        log.info("Resultado calculado. pautaId={} status={} sim={} nao={}", pautaId, status, totalSim, totalNao);
        return new ResultadoResponse(pautaId, totalSim, totalNao, status);
    }
}
