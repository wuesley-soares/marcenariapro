-- MySQL dump 10.13  Distrib 8.0.46, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: railway
-- ------------------------------------------------------
-- Server version	8.4.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ambientes`
--

DROP TABLE IF EXISTS `ambientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ambientes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_criacao` datetime(6) DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `projeto_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs3qttupq0ucqdo2hrqsr9dyds` (`projeto_id`),
  CONSTRAINT `FKs3qttupq0ucqdo2hrqsr9dyds` FOREIGN KEY (`projeto_id`) REFERENCES `projetos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assistencias_tecnicas`
--

DROP TABLE IF EXISTS `assistencias_tecnicas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assistencias_tecnicas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_agendamento` datetime(6) DEFAULT NULL,
  `data_entrega_original` date DEFAULT NULL,
  `data_solicitacao` date DEFAULT NULL,
  `dentro_garantia` bit(1) DEFAULT NULL,
  `descricao` text,
  `status` enum('ABERTA','AGENDADA','CANCELADA','CONCLUIDA','EM_ANDAMENTO') DEFAULT NULL,
  `valor_materiais` double DEFAULT NULL,
  `valor_servico` double DEFAULT NULL,
  `valor_total` double DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  `funcionario_id` bigint DEFAULT NULL,
  `pedido_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKg6lksldo0w2i9ml8m4o7icr9s` (`cliente_id`),
  KEY `FKcbjmpc1cfjpqmij6tnqduspkp` (`funcionario_id`),
  KEY `FKln4bwi6o3weob14pg0f8runeq` (`pedido_id`),
  CONSTRAINT `FKcbjmpc1cfjpqmij6tnqduspkp` FOREIGN KEY (`funcionario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FKg6lksldo0w2i9ml8m4o7icr9s` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  CONSTRAINT `FKln4bwi6o3weob14pg0f8runeq` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cpf_cnpj` varchar(255) DEFAULT NULL,
  `data_cadastro` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `itens_assistencia`
--

DROP TABLE IF EXISTS `itens_assistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_assistencia` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantidade` int DEFAULT NULL,
  `valor_unitario` double DEFAULT NULL,
  `assistencia_id` bigint DEFAULT NULL,
  `material_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe1ufggnm7ntsmf08xfw2mprqn` (`assistencia_id`),
  KEY `FKd3to2c5pl9su3rfr6xscvf2df` (`material_id`),
  CONSTRAINT `FKd3to2c5pl9su3rfr6xscvf2df` FOREIGN KEY (`material_id`) REFERENCES `materiais` (`id`),
  CONSTRAINT `FKe1ufggnm7ntsmf08xfw2mprqn` FOREIGN KEY (`assistencia_id`) REFERENCES `assistencias_tecnicas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `itens_assistencia_tecnica`
--

DROP TABLE IF EXISTS `itens_assistencia_tecnica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_assistencia_tecnica` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantidade` int DEFAULT NULL,
  `assistencia_id` bigint DEFAULT NULL,
  `material_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn6a5pxn17079i15miij56kdjx` (`assistencia_id`),
  KEY `FKsipg1wxtajr4ufe7a1x3nm24r` (`material_id`),
  CONSTRAINT `FKn6a5pxn17079i15miij56kdjx` FOREIGN KEY (`assistencia_id`) REFERENCES `assistencias_tecnicas` (`id`),
  CONSTRAINT `FKsipg1wxtajr4ufe7a1x3nm24r` FOREIGN KEY (`material_id`) REFERENCES `materiais` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `itens_orcamento`
--

DROP TABLE IF EXISTS `itens_orcamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_orcamento` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantidade` decimal(38,2) DEFAULT NULL,
  `unidade` varchar(255) DEFAULT NULL,
  `valor_unitario` decimal(38,2) DEFAULT NULL,
  `material_id` bigint NOT NULL,
  `orcamento_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf9302ip1nx8hnt878cs3t4s35` (`material_id`),
  KEY `FKqlfm2lrgelma3n8kwabcvw9qh` (`orcamento_id`),
  CONSTRAINT `FKf9302ip1nx8hnt878cs3t4s35` FOREIGN KEY (`material_id`) REFERENCES `materiais` (`id`),
  CONSTRAINT `FKqlfm2lrgelma3n8kwabcvw9qh` FOREIGN KEY (`orcamento_id`) REFERENCES `orcamentos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `itens_producao`
--

DROP TABLE IF EXISTS `itens_producao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_producao` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantidade` decimal(38,2) DEFAULT NULL,
  `material_id` bigint DEFAULT NULL,
  `producao_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1iah9hi29c0pwedn5gem4yvgo` (`material_id`),
  KEY `FKfbpyu3fdithsqhko3ls6ykogg` (`producao_id`),
  CONSTRAINT `FK1iah9hi29c0pwedn5gem4yvgo` FOREIGN KEY (`material_id`) REFERENCES `materiais` (`id`),
  CONSTRAINT `FKfbpyu3fdithsqhko3ls6ykogg` FOREIGN KEY (`producao_id`) REFERENCES `producoes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `materiais`
--

DROP TABLE IF EXISTS `materiais`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `materiais` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `descricao` varchar(255) DEFAULT NULL,
  `estoque_minimo` int DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `quantidade` int DEFAULT NULL,
  `valor_unitario` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimentacoes_estoque`
--

DROP TABLE IF EXISTS `movimentacoes_estoque`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimentacoes_estoque` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_movimentacao` datetime(6) DEFAULT NULL,
  `quantidade` int DEFAULT NULL,
  `tipo` enum('ENTRADA','SAIDA') DEFAULT NULL,
  `material_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2ulmhhmplv0q5g7ahynjo3suc` (`material_id`),
  CONSTRAINT `FK2ulmhhmplv0q5g7ahynjo3suc` FOREIGN KEY (`material_id`) REFERENCES `materiais` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orcamentos`
--

DROP TABLE IF EXISTS `orcamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orcamentos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_criacao` datetime(6) DEFAULT NULL,
  `data_validade` datetime(6) DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `margem_lucro` decimal(38,2) DEFAULT NULL,
  `status` enum('APROVADO','PENDENTE','RECUSADO') DEFAULT NULL,
  `valor` decimal(38,2) DEFAULT NULL,
  `valor_mao_obra` decimal(38,2) DEFAULT NULL,
  `ambiente_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKci1c52roidio1t2lruyc0gwb` (`ambiente_id`),
  CONSTRAINT `FKft6ylbkmuciluxw2ujfrodo9w` FOREIGN KEY (`ambiente_id`) REFERENCES `ambientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pagamentos`
--

DROP TABLE IF EXISTS `pagamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagamentos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `caminho_comprovante` varchar(255) DEFAULT NULL,
  `data_pagamento` datetime(6) DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `nome_comprovante` varchar(255) DEFAULT NULL,
  `status` enum('CANCELADO','PAGO','PENDENTE') DEFAULT NULL,
  `valor` decimal(38,2) DEFAULT NULL,
  `pedido_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb0dh6865y8h4irwdrl4ici3kl` (`pedido_id`),
  CONSTRAINT `FKb0dh6865y8h4irwdrl4ici3kl` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_criacao` datetime(6) DEFAULT NULL,
  `data_entrega` date DEFAULT NULL,
  `previsao_entrega` date DEFAULT NULL,
  `status` enum('APROVADO','CANCELADO','ENTREGUE','PENDENTE') DEFAULT NULL,
  `orcamento_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgdtlgtcourwx52rfjeexbpvrs` (`orcamento_id`),
  CONSTRAINT `FKme15slf87t5e4co2a3md0xmfh` FOREIGN KEY (`orcamento_id`) REFERENCES `orcamentos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `producoes`
--

DROP TABLE IF EXISTS `producoes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producoes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_criacao` datetime(6) DEFAULT NULL,
  `status` enum('AGUARDANDO','CANCELADO','EM_PRODUCAO','ENTREGUE','PRONTO') DEFAULT NULL,
  `pedido_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgy3kqphwm4r6esolf4teh3euf` (`pedido_id`),
  CONSTRAINT `FKata9jq81fjnhi07poot0t9i4t` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `projetos`
--

DROP TABLE IF EXISTS `projetos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projetos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_criacao` datetime(6) DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoo6tpavmurkg556s1q7ryldq2` (`cliente_id`),
  CONSTRAINT `FKoo6tpavmurkg556s1q7ryldq2` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `solicitacoes_substituicao`
--

DROP TABLE IF EXISTS `solicitacoes_substituicao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solicitacoes_substituicao` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` enum('APROVADA','PENDENTE','RECUSADA') DEFAULT NULL,
  `item_producao_id` bigint DEFAULT NULL,
  `novo_material_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtbv5fb22q81059yornndys0ra` (`item_producao_id`),
  KEY `FKi4by10ml8xpw0olsr7bkvec3e` (`novo_material_id`),
  CONSTRAINT `FKi4by10ml8xpw0olsr7bkvec3e` FOREIGN KEY (`novo_material_id`) REFERENCES `materiais` (`id`),
  CONSTRAINT `FKtbv5fb22q81059yornndys0ra` FOREIGN KEY (`item_producao_id`) REFERENCES `itens_producao` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) NOT NULL,
  `data_criacao` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `perfil` enum('ADMIN','MARCENEIRO','VENDEDOR') NOT NULL,
  `senha` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkfsp0s1tflm1cwlj8idhqsad0` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-02 23:12:34
