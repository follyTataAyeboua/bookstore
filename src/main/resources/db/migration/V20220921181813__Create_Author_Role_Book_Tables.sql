CREATE TABLE author_roles
(
    author_id BIGINT NOT NULL,
    role_id   BIGINT NOT NULL,
    CONSTRAINT pk_author_roles PRIMARY KEY (author_id, role_id)
);

CREATE TABLE authors
(
    id        SERIAL PRIMARY KEY,
    username  VARCHAR(255) NULL,
    firstname VARCHAR(255) NULL,
    lastname  VARCHAR(255) NULL,
    email     VARCHAR(255) NULL,
    password  VARCHAR(255) NULL
);

CREATE TABLE books
(
    id            SERIAL PRIMARY KEY,
    title         VARCHAR(255) NULL,
    description   TEXT NULL,
    author_id     BIGINT NOT NULL,
    price         DECIMAL(38,22) NULL
);

CREATE TABLE roles
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(20) NULL
);

ALTER TABLE books
    ADD CONSTRAINT FK_BOOKS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES authors (id);

ALTER TABLE author_roles
    ADD CONSTRAINT fk_autrol_on_author FOREIGN KEY (author_id) REFERENCES authors (id);

ALTER TABLE author_roles
    ADD CONSTRAINT fk_autrol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);