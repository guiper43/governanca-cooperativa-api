package br.com.guilherme.governanca_cooperativa_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "integrations.cpf-validator")
public record CpfValidationProperties(
    String url,
    Boolean enabled,
    Boolean fallbackEnabled,
    Set<String> unableCpfs) {

    public CpfValidationProperties {
        unableCpfs = unableCpfs == null ? Set.of() : unableCpfs;
    }

    public boolean isEnabled() {
        return enabled == null || enabled;
    }

    public boolean isFallbackEnabled() {
        return fallbackEnabled != null && fallbackEnabled;
    }
}
