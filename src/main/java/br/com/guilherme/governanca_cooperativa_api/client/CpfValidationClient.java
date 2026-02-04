    package br.com.guilherme.governanca_cooperativa_api.client;
    
import br.com.guilherme.governanca_cooperativa_api.client.dto.CpfValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cpfValidationClient", url = "${integrations.cpf-validator.url}")
public interface CpfValidationClient {
    @GetMapping("/users/{cpf}")
    CpfValidationResponse buscarStatusCpf(@PathVariable("cpf") String cpf);
}
