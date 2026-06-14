package br.com.guilherme.governanca_cooperativa_api.domain.repository;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.UrnaVoto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UrnaVotoRepository extends JpaRepository<UrnaVoto, UUID> {
    long countByPautaIdAndVotoEscolha(UUID pautaId, VotoEscolha votoEscolha);
}
