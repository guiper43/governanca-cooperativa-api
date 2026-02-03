package br.com.guilherme.governanca_cooperativa_api.web.controller.presentation;

import br.com.guilherme.governanca_cooperativa_api.doc.PresentationSessaoControllerDoc;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation.SessaoTelaAssembler;
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
@RequestMapping("/mobile/v1/pautas/{pautaId}/sessoes")
@RequiredArgsConstructor
@Slf4j
public class PresentationSessaoController implements PresentationSessaoControllerDoc {

    private final PautaService pautaService;
    private final SessaoTelaAssembler sessaoTelaAssembler;

    @Override
    @GetMapping("/nova")
    public ResponseEntity<PresentationTelaResponse> getTelaAberturaSessao(@PathVariable UUID pautaId) {
        log.info("Solicitação de tela de abertura de sessão. pautaId={}", pautaId);
        Pauta pauta = pautaService.buscarEntidade(pautaId);
        var tela = sessaoTelaAssembler.montarTelaAbertura(pauta);
        log.info("Tela de abertura de sessão retornada. pautaId={}", pautaId);
        return ResponseEntity.ok(tela);
    }
}
