-- Hvilke meldinger er allerede behandlet? (idempotens)
CREATE TABLE behandlet_melding (
    melding_id          VARCHAR(100) PRIMARY KEY,
    behandlet_tidspunkt TIMESTAMPTZ  NOT NULL
);

-- Siste kjente stemmetall per parti per krets
CREATE TABLE parti_stemmer (
    id          BIGSERIAL    PRIMARY KEY,
    valg_type   VARCHAR(50)  NOT NULL,
    fylke       VARCHAR(100) NOT NULL,
    kommune     VARCHAR(100) NOT NULL,
    krets       VARCHAR(100) NOT NULL,
    parti       VARCHAR(50)  NOT NULL,
    stemmer     INT          NOT NULL CHECK (stemmer >= 0),
    er_endelig  BOOLEAN      NOT NULL,
    melding_id  VARCHAR(100) NOT NULL
);

CREATE INDEX idx_parti_stemmer_krets ON parti_stemmer (valg_type, fylke, kommune, krets);