package br.com.guilherme.governanca_cooperativa_api.web.controller.rest;

import br.com.guilherme.governanca_cooperativa_api.doc.VotoControllerDoc;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.VotoOutput;
import br.com.guilherme.governanca_cooperativa_api.service.VotoService;
import br.com.guilherme.governanca_cooperativa_api.web.assembler.rest.VotoRestAssembler;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static br.com.guilherme.governanca_cooperativa_api.utils.CpfUtils.mascararCpf;

@RestController
@RequestMapping("/v1/pautas/{pautaId}/votos")
@RequiredArgsConstructor
@Validated
@Slf4j
public class VotoController implements VotoControllerDoc {
    private final VotoService service;
    private final VotoRestAssembler votoAssembler;

    @Override
    @PostMapping
    public ResponseEntity<VotoResponse> votar(
        @PathVariable UUID pautaId, @Valid @RequestBody VotoRequest request) {
        log.info("Requisição de voto recebida. pautaId={} associadoId={}", pautaId, mascararCpf(request.associadoId()));
        var input = votoAssembler.toInput(request);
        VotoOutput output = service.votar(pautaId, input);
        var response = votoAssembler.toResponse(output);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
