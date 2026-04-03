package br.com.guilherme.governanca_cooperativa_api.domain.entity;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.ParticipacaoStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registro_participacao", uniqueConstraints = {
        @UniqueConstraint(name = "uk_registro_participacao_pauta_associado", columnNames = { "pauta_id", "associado_id" }),
        @UniqueConstraint(name = "uk_registro_participacao_token_hash", columnNames = "token_hash")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistroParticipacao {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(name = "associado_id", nullable = false, length = 100)
    private String associadoId;

    @Column(name = "token_hash", nullable = false, length = 128)
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ParticipacaoStatus status;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "data_consumo")
    private LocalDateTime dataConsumo;

    private RegistroParticipacao(UUID id, Pauta pauta, String associadoId, String tokenHash,
                                 ParticipacaoStatus status, LocalDateTime dataRegistro, LocalDateTime dataConsumo) {
        this.id = id;
        this.pauta = pauta;
        this.associadoId = associadoId;
        this.tokenHash = tokenHash;
        this.status = status;
        this.dataRegistro = dataRegistro;
        this.dataConsumo = dataConsumo;
    }

    public static RegistroParticipacao emitir(UUID id, Pauta pauta, String associadoId, String tokenHash) {
        return new RegistroParticipacao(id, pauta, associadoId, tokenHash, ParticipacaoStatus.EMITIDO,
                LocalDateTime.now(), null);
    }

    public void consumir() {
        this.status = ParticipacaoStatus.CONSUMIDO;
        this.dataConsumo = LocalDateTime.now();
    }
}
