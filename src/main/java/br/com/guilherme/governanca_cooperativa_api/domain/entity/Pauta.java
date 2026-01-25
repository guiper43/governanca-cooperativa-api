package br.com.guilherme.governanca_cooperativa_api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

@Entity
@Table(name = "pauta")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Pauta {
    @Id
    @Column(name = "id", nullable = false)
    private java.util.UUID id;
    @Column(name = "descricao", nullable = false, columnDefinition = "text")
    private String descricao;

    private Pauta(UUID id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public static Pauta criar(java.util.UUID id, String descricao) {
return new Pauta(id, descricao);
    }

}
