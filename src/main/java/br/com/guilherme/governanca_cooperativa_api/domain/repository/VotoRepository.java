package br.com.guilherme.governanca_cooperativa_api.domain.repository;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Voto;
import br.com.guilherme.governanca_cooperativa_api.domain.enums.VotoEscolha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VotoRepository extends JpaRepository<Voto, UUID> {
    long countByPautaIdAndVotoEscolha(UUID pautaId, VotoEscolha votoEscolha);
}
