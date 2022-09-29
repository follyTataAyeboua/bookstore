CREATE TABLE author_roles
(
    author_id BIGINT NOT NULL,
    role_id   BIGINT NOT NULL,
    CONSTRAINT pk_author_roles PRIMARY KEY (author_id, role_id)
);

CREATE TABLE authors
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    username  VARCHAR(255) NULL,
    firstname VARCHAR(255) NULL,
    lastname  VARCHAR(255) NULL,
    email     VARCHAR(255) NULL,
    password  VARCHAR(255) NULL,
    CONSTRAINT pk_authors PRIMARY KEY (id)
);

CREATE TABLE books
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(255) NULL,
    description   TEXT NULL,
    author_id     BIGINT NOT NULL,
    price         DECIMAL(38,22) NULL,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(20) NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

ALTER TABLE books
    ADD CONSTRAINT FK_BOOKS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES authors (id);

ALTER TABLE author_roles
    ADD CONSTRAINT fk_autrol_on_author FOREIGN KEY (author_id) REFERENCES authors (id);

ALTER TABLE author_roles
    ADD CONSTRAINT fk_autrol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);