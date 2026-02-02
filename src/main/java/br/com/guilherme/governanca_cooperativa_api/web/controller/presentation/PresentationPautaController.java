package br.com.guilherme.governanca_cooperativa_api.web.controller.presentation;

import br.com.guilherme.governanca_cooperativa_api.doc.PresentationPautaControllerDoc;
import br.com.guilherme.governanca_cooperativa_api.web.assembler.presentation.PautaTelaAssembler;
import br.com.guilherme.governanca_cooperativa_api.web.dto.presentation.TelaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mobile/v1/pautas")
@RequiredArgsConstructor
@Slf4j
public class PresentationPautaController implements PresentationPautaControllerDoc {

        private final PautaTelaAssembler pautaTelaAssembler;

        @Override
        @GetMapping("/nova")
        public ResponseEntity<TelaResponse> getTelaCadastroPauta() {
                log.info("Solicitação de tela de cadastro de nova pauta");
                var tela = pautaTelaAssembler.montarTelaCadastro();
                log.info("Tela de cadastro de pauta retornada com sucesso");
                return ResponseEntity.ok(tela);
        }
}
