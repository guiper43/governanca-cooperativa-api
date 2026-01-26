package br.com.guilherme.governanca_cooperativa_api.domain.repository;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    Optional<Sessao> findByPautaId(UUID pauta);
}
