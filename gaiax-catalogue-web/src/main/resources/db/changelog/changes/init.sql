--liquibase formatted sql
--changeset Dilip:1
CREATE TABLE participant_master(
    id UUID NOT NULL,
    did VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_participant_master_id PRIMARY KEY (id)
);

CREATE TABLE service_offer_master(
    id UUID NOT NULL,
    service_offer_id VARCHAR(255) NOT NULL,
    service_sd TEXT NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    participant_id UUID NOT NULL,
    CONSTRAINT fk_participant_id FOREIGN KEY (participant_id) REFERENCES participant_master(id),
    CONSTRAINT pk_service_offer_master_id PRIMARY KEY (id)
);

--changeset Dilip:2
CREATE TABLE ces_process_tracker(
    id UUID NOT NULL,
    ces_id varchar(50) NOT NULL,
    reason varchar(200),
    status INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    UNIQUE(ces_id),
    CONSTRAINT pk_ces_process_tracker_id PRIMARY KEY (id)
);

--changeset Dilip:3
ALTER TABLE ces_process_tracker ALTER COLUMN reason TYPE text USING reason::text;

--changeset Dilip:4
DROP TABLE service_offer_master;
DROP TABLE participant_master;
ALTER TABLE ces_process_tracker ADD COLUMN credential text;