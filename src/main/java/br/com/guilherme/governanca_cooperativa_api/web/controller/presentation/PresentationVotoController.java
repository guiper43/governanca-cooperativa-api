package br.com.guilherme.governanca_cooperativa_api.web.controller.presentation;

import br.com.guilherme.governanca_cooperativa_api.doc.PresentationVotoControllerDoc;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaOutput;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation.VotoTelaAssembler;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.PresentationTelaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/mobile/v1/pautas/{pautaId}/votos")
@RequiredArgsConstructor
@Slf4j
public class PresentationVotoController implements PresentationVotoControllerDoc {

    private final PautaService pautaService;
    private final VotoTelaAssembler votoTelaAssembler;

    @Override
    @GetMapping("/selecao")
    public ResponseEntity<PresentationTelaResponse> getTelaVotacao(@PathVariable UUID pautaId) {
        log.info("Solicitação de tela de votação. pautaId={}", pautaId);
        PautaOutput pauta = pautaService.buscar(pautaId);
        var tela = votoTelaAssembler.montarTelaVotacao(pauta);
        log.info("Tela de votação retornada. pautaId={}", pautaId);
        return ResponseEntity.ok(tela);
    }
}
