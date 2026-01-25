package br.com.guilherme.governanca_cooperativa_api.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessao")
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Sessao {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_fechamento", nullable = false)
    private LocalDateTime dataFechamento;

    private Sessao(UUID id, Pauta pauta, LocalDateTime dataAbertura, LocalDateTime dataFechamento) {
        this.id = id;
        this.pauta = pauta;
        this.dataAbertura = dataAbertura;
        this.dataFechamento = dataFechamento;
    }

    public static Sessao criar(UUID id, Pauta pauta, LocalDateTime dataAbertura, LocalDateTime dataFechamento) {
        return new Sessao(id, pauta, dataAbertura, dataFechamento);
    }

}
