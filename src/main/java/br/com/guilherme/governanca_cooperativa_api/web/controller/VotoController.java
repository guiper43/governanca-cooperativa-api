package br.com.guilherme.governanca_cooperativa_api.web.controller;

import br.com.guilherme.governanca_cooperativa_api.service.VotoService;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoRequest;
import static br.com.guilherme.governanca_cooperativa_api.utils.CpfUtils.mascararCpf;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import br.com.guilherme.governanca_cooperativa_api.doc.VotoControllerDoc;

@RestController
@RequestMapping("/v1/pautas/{pautaId}/votos")
@RequiredArgsConstructor
@Validated
@Slf4j
public class VotoController implements VotoControllerDoc {
    private final VotoService service;

    @PostMapping
    public ResponseEntity<VotoResponse> votar(
            @PathVariable UUID pautaId, @Valid @RequestBody VotoRequest request) {
        log.info("Requisição de voto recebida. pautaId={} associadoId={}", pautaId, mascararCpf(request.associadoId()));
        var response = service.votar(pautaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
