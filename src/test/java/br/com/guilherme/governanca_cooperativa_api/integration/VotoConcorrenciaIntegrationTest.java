package br.com.guilherme.governanca_cooperativa_api.integration;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "integrations.cpf-validator.enabled=false"
})
@AutoConfigureMockMvc
class VotoConcorrenciaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CPF_TESTE = "88484415058";

    @Test
    void votar_concorrente_garanteApenasUmVotoComputado() throws Exception {
        // 1. Criar Pauta
        PautaRequest pautaReq = new PautaRequest("Pauta Concorrencia Test");
        MvcResult pautaResult = mockMvc.perform(post("/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pautaReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String pautaIdStr = objectMapper.readTree(pautaResult.getResponse().getContentAsString()).get("id").asText();
        UUID pautaId = UUID.fromString(pautaIdStr);

        // 2. Abrir Sessão
        SessaoRequest sessaoReq = new SessaoRequest(5);
        mockMvc.perform(post("/v1/pautas/{id}/sessoes", pautaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessaoReq)))
                .andExpect(status().isCreated());

        // 3. Executar votos concorrentes para o mesmo CPF
        int threadsCount = 8;
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch latch = new CountDownLatch(1);
        
        VotoRequest votoReq = new VotoRequest(CPF_TESTE, VotoEscolha.SIM);
        String votoBody = objectMapper.writeValueAsString(votoReq);

        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < threadsCount; i++) {
            futures.add(executor.submit(() -> {
                try {
                    latch.await(); // Sincroniza início de todas as threads
                    MvcResult result = mockMvc.perform(post("/v1/pautas/{id}/votos", pautaId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(votoBody))
                            .andReturn();
                    return result.getResponse().getStatus();
                } catch (Exception e) {
                    return 500;
                }
            }));
        }

        // Libera todas as threads simultaneamente
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // 4. Agrupar resultados dos status HTTP
        int successCount = 0;
        int errorCount = 0;

        for (Future<Integer> future : futures) {
            int status = future.get();
            if (status == 201) {
                successCount++;
            } else if (status == 422 || status == 409) {
                errorCount++;
            }
        }

        // Garante que exatamente 1 voto foi criado e os outros falharam por concorrência/duplicidade
        assertEquals(1, successCount, "Deveria haver exatamente 1 voto registrado com sucesso");
        assertEquals(threadsCount - 1, errorCount, "As outras requisições concorrentes deveriam ter falhado");
    }
}
