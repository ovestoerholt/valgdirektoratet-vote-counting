CREATE TABLE revisjonslogg (
    id          BIGSERIAL    PRIMARY KEY,
    melding_id  VARCHAR(100),
    routing_key VARCHAR(255) NOT NULL,
    payload     TEXT         NOT NULL,   -- rå JSON, nøyaktig slik den ble mottatt
    mottatt     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Gjør loggen uforanderlig: UPDATE og DELETE avvises av databasen selv.
CREATE FUNCTION hindre_endring() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'revisjonslogg er uforanderlig: % er ikke tillatt', TG_OP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER revisjonslogg_uforanderlig
    BEFORE UPDATE OR DELETE ON revisjonslogg
    FOR EACH ROW EXECUTE FUNCTION hindre_endring();
