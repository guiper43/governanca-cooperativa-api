package br.com.guilherme.governanca_cooperativa_api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "pauta")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Pauta {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
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
