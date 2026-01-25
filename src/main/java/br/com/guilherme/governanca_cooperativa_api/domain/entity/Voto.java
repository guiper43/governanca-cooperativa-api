package br.com.guilherme.governanca_cooperativa_api.domain.entity;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
    name = "voto", uniqueConstraints = @UniqueConstraint(
    name = "uk_voto_pauta_associado",
    columnNames = {"pauta_id", "associado_id"}
)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Voto {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;
    @Column(name = "associado_id", nullable = false, length = 100)
    private String associadoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "voto_escolha", nullable = false, length = 3)
    private VotoEscolha votoEscolha;

    private Voto(UUID id, Pauta pauta, String associadoId, VotoEscolha votoEscolha) {
        this.id = id;
        this.pauta = pauta;
        this.associadoId = associadoId;
        this.votoEscolha = votoEscolha;
    }

    public static Voto criar(UUID id, Pauta pauta, String associadoId, VotoEscolha votoEscolha) {
        return new Voto(id, pauta, associadoId, votoEscolha);

    }
}
