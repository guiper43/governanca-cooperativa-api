package br.com.guilherme.governanca_cooperativa_api.integration;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.presentation.TipoTelaMobile;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta.PautaRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.sessao.SessaoRequest;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.voto.VotoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "integrations.cpf-validator.enabled=false"
})
@AutoConfigureMockMvc
@Transactional
class FluxoVotacaoMobileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CPF_VALIDO_TESTE = "88484415058";

    @Test
    void fluxoCompleto_criarPauta_abrirSessao_verTelaMobile_votar() throws Exception {
        mockMvc.perform(get("/mobile/v1/pautas/nova"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo", is(TipoTelaMobile.FORMULARIO.name())))
                .andExpect(jsonPath("$.itens[0].idCampoTexto", is("descricao")));

        PautaRequest pautaReq = new PautaRequest("Nova Pauta de Teste Integration");
        MvcResult pautaResult = mockMvc.perform(post("/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pautaReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String pautaIdStr = objectMapper.readTree(pautaResult.getResponse().getContentAsString()).get("id").asText();
        UUID pautaId = UUID.fromString(pautaIdStr);

        SessaoRequest sessaoReq = new SessaoRequest(60);
        mockMvc.perform(post("/v1/pautas/{id}/sessoes", pautaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessaoReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/mobile/v1/pautas/{id}/votos/selecao", pautaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo", is(TipoTelaMobile.SELECAO.name())))
                .andExpect(jsonPath("$.titulo", containsString("Nova Pauta")))
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.itens[0].body.associadoId", is("")))
                .andExpect(jsonPath("$.itens[1].body.associadoId", is("")));

        VotoRequest votoReq = new VotoRequest(CPF_VALIDO_TESTE, VotoEscolha.SIM);

        mockMvc.perform(post("/v1/pautas/{id}/votos", pautaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votoReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.votoEscolha", is("SIM")));

        mockMvc.perform(get("/v1/pautas/{id}/resultado", pautaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSim", is(1)))
                .andExpect(jsonPath("$.totalNao", is(0)));
    }
}
