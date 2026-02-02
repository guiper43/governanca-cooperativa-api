package br.com.guilherme.governanca_cooperativa_api.doc;

import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta.PautaRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta.PautaResponse;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.resultado.ResultadoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Pautas", description = "Endpoints para gerenciamento de pautas e consulta de resultados")
public interface PautaControllerDoc {

        @Operation(summary = "Criar Pauta", description = "Cadastra uma nova pauta para votação futura.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos")
        })
        ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request);

        @Operation(summary = "Buscar Pauta por ID", description = "Recupera os detalhes de uma pauta específica.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Pauta encontrada"),
                        @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
        })
        ResponseEntity<PautaResponse> buscarPorId(@Parameter(description = "ID único da pauta") @PathVariable UUID id);

        @Operation(summary = "Consultar Resultado", description = "Exibe o resultado parcial ou final da votação de uma pauta.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Resultado calculado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada")
        })
        ResponseEntity<ResultadoResponse> consultarResultado(
                        @Parameter(description = "ID da pauta para consulta") @PathVariable UUID pautaId);
}
