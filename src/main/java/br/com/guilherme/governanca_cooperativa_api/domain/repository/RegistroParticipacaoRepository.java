package br.com.guilherme.governanca_cooperativa_api.domain.repository;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.RegistroParticipacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegistroParticipacaoRepository extends JpaRepository<RegistroParticipacao, UUID> {
    boolean existsByPautaIdAndAssociadoId(UUID pautaId, String associadoId);
}
