CREATE TABLE pauta (
    id UUID PRIMARY KEY,
    descricao TEXT NOT NULL
);

CREATE TABLE sessao (
    id UUID PRIMARY KEY,
    pauta_id UUID NOT NULL UNIQUE,
    data_abertura TIMESTAMP NOT NULL,
    data_fechamento TIMESTAMP NOT NULL,
    CONSTRAINT fk_sessao_pauta FOREIGN KEY (pauta_id) REFERENCES pauta (id) ON DELETE CASCADE
);

CREATE TABLE voto (
    id UUID PRIMARY KEY,
    pauta_id UUID NOT NULL,
    associado_id VARCHAR(100) NOT NULL,
    voto_escolha VARCHAR(3) NOT NULL,
    CONSTRAINT fk_voto_pauta FOREIGN KEY (pauta_id) REFERENCES pauta (id) ON DELETE CASCADE,
    CONSTRAINT uk_voto_pauta_associado UNIQUE (pauta_id, associado_id)
);

CREATE INDEX idx_voto_pauta_id ON voto (pauta_id);