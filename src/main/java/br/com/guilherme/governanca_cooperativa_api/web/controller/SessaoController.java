package br.com.guilherme.governanca_cooperativa_api.web.controller;

import br.com.guilherme.governanca_cooperativa_api.services.SessaoService;
import br.com.guilherme.governanca_cooperativa_api.web.dto.sessao.SessaoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.sessao.SessaoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pautas/{pautaId}/sessoes")
@RequiredArgsConstructor
@Validated
public class SessaoController {
    private final SessaoService service;

    @PostMapping
    public ResponseEntity<SessaoResponse> abrir(@PathVariable UUID pautaId, @Valid @RequestBody(required = false) SessaoRequest request) {
        var response = service.abrir(pautaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
