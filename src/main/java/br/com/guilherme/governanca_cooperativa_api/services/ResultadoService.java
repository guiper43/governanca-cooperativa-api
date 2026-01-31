package br.com.guilherme.governanca_cooperativa_api.services;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.ResultadoStatus;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.VotoRepository;
import br.com.guilherme.governanca_cooperativa_api.web.dto.resultado.ResultadoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResultadoService {
    private final VotoRepository votoRepository;
    private final SessaoRepository sessaoRepository;
    private final PautaService pautaService;

    public ResultadoResponse consultar(UUID pautaId) {
        pautaService.buscarEntidade(pautaId);

        Sessao sessao = sessaoRepository.findByPautaId(pautaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada para a pauta"));

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

        return new ResultadoResponse(pautaId, totalSim, totalNao, status);
    }
}
