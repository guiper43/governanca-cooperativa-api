package br.com.guilherme.governanca_cooperativa_api.web.controller;

import br.com.guilherme.governanca_cooperativa_api.services.PautaService;
import br.com.guilherme.governanca_cooperativa_api.services.ResultadoService;
import br.com.guilherme.governanca_cooperativa_api.web.dto.pauta.PautaRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.pauta.PautaResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.resultado.ResultadoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pautas")
@RequiredArgsConstructor
@Validated
public class PautaController {
    private final PautaService pautaService;
    private final ResultadoService resultadoService;

    @PostMapping
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
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
