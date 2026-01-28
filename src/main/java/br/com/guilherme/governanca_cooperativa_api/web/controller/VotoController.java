package br.com.guilherme.governanca_cooperativa_api.web.controller;

import br.com.guilherme.governanca_cooperativa_api.services.VotoService;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.voto.VotoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pautas/{pautaId}/votos")
@RequiredArgsConstructor
@Validated
public class VotoController {
    private final VotoService service;

    @PostMapping
    public ResponseEntity<VotoResponse> votar(
        @PathVariable UUID pautaId, @Valid @RequestBody VotoRequest request) {
        var response = service.votar(pautaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
