package br.com.guilherme.governanca_cooperativa_api.service;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.Pauta;
import br.com.guilherme.governanca_cooperativa_api.domain.entity.Sessao;
import br.com.guilherme.governanca_cooperativa_api.domain.repository.SessaoRepository;
import br.com.guilherme.governanca_cooperativa_api.exception.BusinessException;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.SessaoInput;
import br.com.guilherme.governanca_cooperativa_api.domain.dto.SessaoOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessaoService {
    private final SessaoRepository repository;
    private final PautaService pautaService;

    public SessaoOutput abrir(UUID pautaId, SessaoInput request) {
        log.info("Iniciando abertura de sessão. pautaId={} duracaoMinutos={}", pautaId, request.duracaoMinutos());

        int duracao = request.duracaoMinutos() == null ? 1 : request.duracaoMinutos();

        if (duracao <= 0) {
            log.warn("Tentativa de abrir sessão com duração inválida. pautaId={} duracao={}", pautaId, duracao);
            throw new BusinessException("Duração inválida");
        }

        if (repository.findByPautaId(pautaId).isPresent()) {
            log.warn("Tentativa de abrir sessão já existente. pautaId={}", pautaId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sessão já existe para a pauta");
        }

        Pauta pauta = pautaService.buscarEntidade(pautaId);

        LocalDateTime abertura = LocalDateTime.now();
        LocalDateTime fechamento = abertura.plusMinutes(duracao);
        UUID id = UUID.randomUUID();
        Sessao sessao = Sessao.criar(id, pauta, abertura, fechamento);
        repository.save(sessao);
        log.info("Sessão aberta com sucesso. sessaoId={} pautaId={} fechamento={}", sessao.getId(), pautaId,
                sessao.getDataFechamento());
        return new SessaoOutput(sessao.getId(), pauta.getId(), sessao.getDataAbertura(), sessao.getDataFechamento());
    }

}