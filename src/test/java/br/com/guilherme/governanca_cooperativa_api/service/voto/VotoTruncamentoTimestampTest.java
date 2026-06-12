package br.com.guilherme.governanca_cooperativa_api.service.voto;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.RegistroParticipacao;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.UrnaVoto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static br.com.guilherme.governanca_cooperativa_api.utils.DomainTestDataFactory.pautaPadrao;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VotoTruncamentoTimestampTest {

    @Test
    void depositar_urnaVoto_deveTruncarDataParaMinutos() {
        Pauta pauta = pautaPadrao(UUID.randomUUID());
        UrnaVoto urnaVoto = UrnaVoto.depositar(UUID.randomUUID(), pauta, VotoEscolha.SIM);
        
        assertEquals(0, urnaVoto.getDataDeposito().getSecond(), "Segundos do depósito devem ser zerados");
        assertEquals(0, urnaVoto.getDataDeposito().getNano(), "Nanosegundos do depósito devem ser zerados");
    }

    @Test
    void consumir_registroParticipacao_deveTruncarDataParaMinutos() {
        Pauta pauta = pautaPadrao(UUID.randomUUID());
        RegistroParticipacao registro = RegistroParticipacao.emitir(UUID.randomUUID(), pauta, "12345678901", "someTokenHash");
        registro.consumir();
        
        assertEquals(0, registro.getDataConsumo().getSecond(), "Segundos do consumo devem ser zerados");
        assertEquals(0, registro.getDataConsumo().getNano(), "Nanosegundos do consumo devem ser zerados");
    }
}
