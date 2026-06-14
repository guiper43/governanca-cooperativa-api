package br.com.guilherme.governanca_cooperativa_api.domain.entity;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "urna_voto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UrnaVoto {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Enumerated(EnumType.STRING)
    @Column(name = "voto_escolha", nullable = false, length = 3)
    private VotoEscolha votoEscolha;

    @Column(name = "data_deposito", nullable = false)
    private LocalDateTime dataDeposito;

    private UrnaVoto(UUID id, Pauta pauta, VotoEscolha votoEscolha, LocalDateTime dataDeposito) {
        this.id = id;
        this.pauta = pauta;
        this.votoEscolha = votoEscolha;
        this.dataDeposito = dataDeposito;
    }

    public static UrnaVoto depositar(UUID id, Pauta pauta, VotoEscolha votoEscolha) {
        return new UrnaVoto(id, pauta, votoEscolha, LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
    }
}
