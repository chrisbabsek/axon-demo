CREATE TABLE IF NOT EXISTS DomainEventEntry (
    globalIndex BIGSERIAL NOT NULL,
    aggregateIdentifier VARCHAR(255) NOT NULL,
    sequenceNumber BIGINT NOT NULL,
    type VARCHAR(255),
    eventIdentifier VARCHAR(255) NOT NULL,
    metaData bytea,
    payload bytea NOT NULL,
    payloadRevision VARCHAR(255),
    payloadType VARCHAR(255) NOT NULL,
    timeStamp VARCHAR(255) NOT NULL,
    PRIMARY KEY (globalIndex),
    UNIQUE (aggregateIdentifier, sequenceNumber),
    UNIQUE (eventIdentifier)
    );

CREATE TABLE IF NOT EXISTS SnapshotEventEntry (
    aggregateIdentifier VARCHAR(255) NOT NULL,
    sequenceNumber BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    eventIdentifier VARCHAR(255) NOT NULL,
    metaData bytea,
    payload bytea NOT NULL,
    payloadRevision VARCHAR(255),
    payloadType VARCHAR(255) NOT NULL,
    timeStamp VARCHAR(255) NOT NULL,
    PRIMARY KEY (aggregateIdentifier, sequenceNumber),
    UNIQUE (eventIdentifier)
    );

CREATE TABLE IF NOT EXISTS TokenEntry (
    processorName VARCHAR(255) NOT NULL,
    segment INTEGER NOT NULL,
    token bytea NULL,
    tokenType VARCHAR(255) NULL,
    timestamp VARCHAR(255) NULL,
    owner VARCHAR(255) NULL,
    PRIMARY KEY (processorName,segment)
    );

CREATE TABLE IF NOT EXISTS SagaEntry (
    sagaId VARCHAR(255) NOT NULL,
    revision VARCHAR(255),
    sagaType VARCHAR(255),
    serializedSaga BYTEA,
    PRIMARY KEY (sagaId)
    );

CREATE TABLE IF NOT EXISTS bank_account_projection
(
    id BIGSERIAL NOT NULL,
    bank_account_id VARCHAR(255) NOT NULL,
    owner_name      TEXT         NOT NULL,
    balance FLOAT NOT NULL,
    transactions    JSONB        NOT NULL,
    PRIMARY KEY (id)
);
