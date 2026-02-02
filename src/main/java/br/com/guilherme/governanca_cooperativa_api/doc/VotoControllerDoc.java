package br.com.guilherme.governanca_cooperativa_api.doc;

import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoResponse;
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

@Tag(name = "Votos", description = "Registro de votos dos associados")
public interface VotoControllerDoc {

        @Operation(summary = "Registrar Voto", description = "Recebe o voto de um associado em uma pauta. Verifica se a sessão está aberta e o associado apto.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados do voto inválidos"),
                        @ApiResponse(responseCode = "404", description = "Pauta ou sessão não encontrada"),
                        @ApiResponse(responseCode = "422", description = "Regra de negócio violada (Sessão encerrada, Associado inapto, Voto duplicado)")
        })
        ResponseEntity<VotoResponse> votar(
                        @Parameter(description = "ID da pauta em votação") @PathVariable UUID pautaId,
                        @Valid @RequestBody VotoRequest request);
}
