package br.com.guilherme.governanca_cooperativa_api.web.controller.rest;

import br.com.guilherme.governanca_cooperativa_api.doc.PautaControllerDoc;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.PautaOutput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.ResultadoOutput;
import br.com.guilherme.governanca_cooperativa_api.service.PautaService;
import br.com.guilherme.governanca_cooperativa_api.service.ResultadoService;
import br.com.guilherme.governanca_cooperativa_api.web.assembler.rest.PautaRestAssembler;
import br.com.guilherme.governanca_cooperativa_api.web.assembler.rest.ResultadoRestAssembler;
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

@RestController
@RequestMapping("/v1/pautas")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PautaController implements PautaControllerDoc {
    private final PautaService pautaService;
    private final ResultadoService resultadoService;
    private final PautaRestAssembler pautaAssembler;
    private final ResultadoRestAssembler resultadoAssembler;

    @Override
    @PostMapping
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
        log.info("Requisição para criar pauta recebida. descricao='{}'", request.descricao());
        var input = pautaAssembler.toInput(request);
        PautaOutput output = pautaService.criar(input);
        var response = pautaAssembler.toResponse(output);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable UUID id) {
        log.info("Requisição para buscar pauta recebida. id={}", id);
        PautaOutput output = pautaService.buscar(id);
        var response = pautaAssembler.toResponse(output);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{pautaId}/resultado")
    public ResponseEntity<ResultadoResponse> consultarResultado(@PathVariable UUID pautaId) {
        log.info("Requisição para consultar resultado recebida. pautaId={}", pautaId);
        ResultadoOutput output = resultadoService.consultar(pautaId);
        var response = resultadoAssembler.toResponse(output);
        return ResponseEntity.ok(response);
    }
}
