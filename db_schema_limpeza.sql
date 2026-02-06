USE airbntruta;

-- Desativa verificação de chaves estrangeiras para poder apagar sem erro
SET FOREIGN_KEY_CHECKS = 0;

-- Apaga os dados das tabelas (Ordem não importa com FK desativada)
TRUNCATE TABLE interesse;
TRUNCATE TABLE hospedagem_servico;
TRUNCATE TABLE hospedagem;
TRUNCATE TABLE fugitivo;
TRUNCATE TABLE hospedeiro;
-- Se tiver tabela de serviço e quiser limpar:
-- TRUNCATE TABLE servico;

-- Reativa a verificação de chaves estrangeiras
SET FOREIGN_KEY_CHECKS = 1;

-- Confirmação
SELECT 'Banco de Dados Limpo com Sucesso!' AS Status;