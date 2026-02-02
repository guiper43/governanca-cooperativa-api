package br.com.guilherme.governanca_cooperativa_api.web.controller.rest;

import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.service.ResultadoService;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta.PautaRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta.PautaResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.resultado.ResultadoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import br.com.guilherme.governanca_cooperativa_api.doc.PautaControllerDoc;

@RestController
@RequestMapping("/v1/pautas")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PautaController implements PautaControllerDoc {
    private final PautaService pautaService;
    private final ResultadoService resultadoService;

    @PostMapping
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
        log.info("Requisição para criar pauta recebida.");
        var response = pautaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable UUID id) {
        var response = pautaService.buscar(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pautaId}/resultado")
    public ResponseEntity<ResultadoResponse> consultarResultado(@PathVariable UUID pautaId) {
        var response = resultadoService.consultar(pautaId);
        return ResponseEntity.ok(response);
    }
}
