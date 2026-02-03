package br.com.guilherme.governanca_cooperativa_api.utils;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.web.dto.rest.pauta.PautaRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public class DomainTestDataFactory {
    public static final LocalDateTime FUTURO_BASE = LocalDateTime.of(2099, 1, 1, 10, 0);
    public static final LocalDateTime PASSADO_BASE = LocalDateTime.of(2000, 1, 1, 10, 0);

    private DomainTestDataFactory() {
    }

    public static UUID uuid(String valor) {
        return UUID.fromString(valor);
    }

    public static Pauta pautaPadrao(UUID pautaId) {
        return pauta(pautaId, "Pauta de teste");
    }

    public static Pauta pauta(UUID pautaId, String descricao) {
        return Pauta.criar(pautaId, descricao);
    }

    public static Sessao sessaoAberta(Pauta pauta, UUID sessaoId) {
        return Sessao.criar(sessaoId, pauta, FUTURO_BASE.minusMinutes(1), FUTURO_BASE.plusMinutes(5));
    }

    public static Sessao sessaoEncerrada(Pauta pauta, UUID sessaoId) {
        return Sessao.criar(sessaoId, pauta, PASSADO_BASE.minusMinutes(10), PASSADO_BASE.minusMinutes(1));
    }

    public static PautaRequest pautaRequestPadrao() {
        return new PautaRequest("Pauta teste");
    }

    public static PautaRequest pautaRequest(String descricao) {
        return new PautaRequest(descricao);
    }

}
