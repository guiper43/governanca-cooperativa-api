package br.com.guilherme.governanca_cooperativa_api.doc;

import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.sessao.SessaoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.sessao.SessaoResponse;
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

@Tag(name = "Sessão", description = "Gerenciamento de sessões de votação")
public interface SessaoControllerDoc {

        @Operation(summary = "Abrir Sessão", description = "Abre uma sessão de votação para uma pauta específica. Se a duração não for informada, o padrão é 1 minuto.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Duração inválida ou dados incorretos"),
                        @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
                        @ApiResponse(responseCode = "409", description = "Já existe uma sessão para esta pauta")
        })
        ResponseEntity<SessaoResponse> abrir(
                        @Parameter(description = "ID da pauta que receberá a sessão") @PathVariable UUID pautaId,
                        @Valid @RequestBody(required = false) SessaoRequest request);
}
