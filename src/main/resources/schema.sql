CREATE TABLE IF NOT EXISTS bank_account_projection
(
    id               BIGSERIAL PRIMARY KEY,
    bank_account_id  VARCHAR(255) NOT NULL,
    owner_name       TEXT         NOT NULL,
    balance          NUMERIC(19, 2) NOT NULL,
    transactions     JSONB        NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS bank_account_projection_bank_account_id_idx
    ON bank_account_projection (bank_account_id);
