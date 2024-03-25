CREATE TABLE IF NOT EXISTS  users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS  category (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS  event (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    annotation TEXT NOT NULL,
    category_id BIGINT REFERENCES category(id) ,
    paid BOOLEAN,
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    participant_limit INT,
    state VARCHAR(255),
    created TIMESTAMP,
    lat FLOAT,
    lon FLOAT,
    request_moderation BOOLEAN
);

CREATE TABLE IF NOT EXISTS  participation_request (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    requester_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    event_id BIGINT REFERENCES event(id) ON DELETE CASCADE,
    status VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL,
    UNIQUE(requester_id, event_id)
);

CREATE TABLE IF NOT EXISTS  compilation (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    pinned BOOLEAN
);

CREATE TABLE IF NOT EXISTS  compilation_events (
    compilation_id BIGINT REFERENCES compilation(id) ON DELETE CASCADE,
    event_id BIGINT REFERENCES event(id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);
