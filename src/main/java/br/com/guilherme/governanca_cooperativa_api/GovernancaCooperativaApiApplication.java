package br.com.guilherme.governanca_cooperativa_api;

import br.com.guilherme.governanca_cooperativa_api.config.CpfValidationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(CpfValidationProperties.class)
public class GovernancaCooperativaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernancaCooperativaApiApplication.class, args);
    }

}
