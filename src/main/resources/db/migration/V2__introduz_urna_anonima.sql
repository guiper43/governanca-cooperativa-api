DROP TABLE IF EXISTS voto CASCADE;

CREATE TABLE registro_participacao (
    id UUID PRIMARY KEY,
    pauta_id UUID NOT NULL,
    associado_id VARCHAR(100) NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    status VARCHAR(20) NOT NULL,
    protocolo_publico UUID NOT NULL,
    data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_consumo TIMESTAMP NULL,
    CONSTRAINT fk_registro_participacao_pauta
        FOREIGN KEY (pauta_id) REFERENCES pauta (id) ON DELETE CASCADE,
    CONSTRAINT uk_registro_participacao_pauta_associado
        UNIQUE (pauta_id, associado_id),
    CONSTRAINT uk_registro_participacao_token_hash
        UNIQUE (token_hash),
    CONSTRAINT uk_registro_participacao_protocolo_publico
        UNIQUE (protocolo_publico),
    CONSTRAINT ck_registro_participacao_status
        CHECK (status IN ('EMITIDO', 'CONSUMIDO'))
);

CREATE INDEX idx_registro_participacao_pauta_id
    ON registro_participacao (pauta_id);

CREATE TABLE urna_voto (
    id UUID PRIMARY KEY,
    pauta_id UUID NOT NULL,
    voto_escolha VARCHAR(3) NOT NULL,
    data_deposito TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_urna_voto_pauta
        FOREIGN KEY (pauta_id) REFERENCES pauta (id) ON DELETE CASCADE,
    CONSTRAINT ck_urna_voto_escolha
        CHECK (voto_escolha IN ('SIM', 'NAO'))
);

CREATE INDEX idx_urna_voto_pauta_id
    ON urna_voto (pauta_id);
