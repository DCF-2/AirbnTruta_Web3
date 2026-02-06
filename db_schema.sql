-- 1. Cria o Banco de Dados
CREATE DATABASE IF NOT EXISTS airbntruta;
USE airbntruta;

-- 2. Tabela Hospedeiro (Quem aluga a casa)
CREATE TABLE IF NOT EXISTS hospedeiro (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    vulgo VARCHAR(100),
    contato VARCHAR(100),
    senha VARCHAR(100)
);

-- 3. Tabela Fugitivo (Quem procura esconderijo)
CREATE TABLE IF NOT EXISTS fugitivo (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    vulgo VARCHAR(100),
    especialidade VARCHAR(100),
    faccao VARCHAR(100),
    descricao TEXT,
    senha VARCHAR(100)
);

-- 4. Tabela Servico (Limpeza, Segurança, etc)
CREATE TABLE IF NOT EXISTS servico (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    tipo VARCHAR(50),
    descricao TEXT
);

-- 5. Tabela Hospedagem (O esconderijo)
CREATE TABLE IF NOT EXISTS hospedagem (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    descricao_curta VARCHAR(100),
    descricao_longa TEXT,
    localizacao VARCHAR(255),
    diaria DOUBLE,
    inicio DATE,
    fim DATE,
    hospedeiro_id INT NOT NULL,
    fugitivo_id INT, -- Pode ser NULL se ninguém alugou ainda
    FOREIGN KEY (hospedeiro_id) REFERENCES hospedeiro(codigo),
    FOREIGN KEY (fugitivo_id) REFERENCES fugitivo(codigo)
);

-- 6. Tabela de Ligação (Hospedagem tem vários Serviços)
CREATE TABLE IF NOT EXISTS hospedagem_servico (
    hospedagem_id INT NOT NULL,
    servico_id INT NOT NULL,
    PRIMARY KEY (hospedagem_id, servico_id),
    FOREIGN KEY (hospedagem_id) REFERENCES hospedagem(codigo),
    FOREIGN KEY (servico_id) REFERENCES servico(codigo)
);

-- 7. Tabela Interesse (Quando um fugitivo quer uma casa)
CREATE TABLE IF NOT EXISTS interesse (
    codigo INT AUTO_INCREMENT PRIMARY KEY,
    realizado BIGINT, -- Timestamp da data
    proposta VARCHAR(255),
    tempo_permanencia INT,
    fugitivo_id INT NOT NULL,
    hospedagem_id INT NOT NULL,
    FOREIGN KEY (fugitivo_id) REFERENCES fugitivo(codigo),
    FOREIGN KEY (hospedagem_id) REFERENCES hospedagem(codigo)
);

-- Inserindo um Hospedeiro de teste (opcional, pra facilitar seu login)
INSERT INTO hospedeiro (nome, vulgo, contato, senha) VALUES ('Davi Admin', 'O Chefe', '8199999999', '1234');