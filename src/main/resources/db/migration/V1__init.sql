-- V1__init.sql
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    message_id VARCHAR(255) NOT NULL UNIQUE,
    queue_name VARCHAR(100) NOT NULL,
    content TEXT,
    message_type VARCHAR(50),
    received_at TIMESTAMP NOT NULL,
    processed BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP,

    CONSTRAINT messages_message_id_unique UNIQUE (message_id)
);

CREATE INDEX idx_messages_received_at ON messages (received_at);
CREATE INDEX idx_messages_queue_name ON messages (queue_name);
CREATE INDEX idx_messages_processed ON messages (processed);
CREATE INDEX idx_messages_message_type ON messages (message_type);

CREATE TABLE partners (
    id BIGSERIAL PRIMARY KEY,
    alias VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    direction VARCHAR(20) NOT NULL,
    application VARCHAR(100),
    processed_flow_type VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT partners_alias_unique UNIQUE (alias),
    CONSTRAINT partners_direction_check CHECK (direction IN ('INBOUND', 'OUTBOUND')),
    CONSTRAINT partners_processed_flow_type_check CHECK (processed_flow_type IN ('MESSAGE', 'ALERTING', 'NOTIFICATION'))
);

CREATE INDEX idx_partners_alias ON partners (alias);
CREATE INDEX idx_partners_direction ON partners (direction);
CREATE INDEX idx_partners_processed_flow_type ON partners (processed_flow_type);

-- Insertion de quelques données de test pour les partenaires
INSERT INTO partners (alias, type, direction, application, processed_flow_type, description, created_at)
VALUES
    ('PARTNER1', 'BANK', 'INBOUND', 'CRM', 'MESSAGE', 'Partenaire bancaire principal', NOW()),
    ('PARTNER2', 'PAYMENT', 'OUTBOUND', 'PAYMENT_GATEWAY', 'MESSAGE', 'Passerelle de paiement', NOW()),
    ('PARTNER3', 'NOTIFICATION', 'OUTBOUND', 'NOTIFICATION_SERVICE', 'NOTIFICATION', 'Service de notification', NOW());

-- Create index for content search (using GIN index with tsvector for full-text search in PostgreSQL)
CREATE INDEX idx_messages_content_search ON messages USING GIN (to_tsvector('french', content));