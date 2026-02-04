package br.com.guilherme.governanca_cooperativa_api.web.controller.rest;

import br.com.guilherme.governanca_cooperativa_api.service.SessaoService;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.sessao.SessaoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.sessao.SessaoResponse;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.SessaoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.SessaoOutput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import br.com.guilherme.governanca_cooperativa_api.doc.SessaoControllerDoc;

@RestController
@RequestMapping("/v1/pautas/{pautaId}/sessoes")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SessaoController implements SessaoControllerDoc {
    private final SessaoService service;

    @Override
    @PostMapping
    public ResponseEntity<SessaoResponse> abrir(@PathVariable UUID pautaId,
            @Valid @RequestBody(required = false) SessaoRequest request) {
        log.info("Requisição para abrir sessão recebida. pautaId={}", pautaId);
        var input = new SessaoInput(request != null ? request.duracaoMinutos() : null);
        SessaoOutput output = service.abrir(pautaId, input);
        var response = new SessaoResponse(output.id(), output.pautaId(), output.dataAbertura(),
                output.dataFechamento());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
