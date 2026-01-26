package br.com.guilherme.governanca_cooperativa_api.domain.repository;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, UUID> {

}
