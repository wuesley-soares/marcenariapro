-- MySQL dump 10.13  Distrib 8.4.11, for Linux (x86_64)
--
-- Host: localhost    Database: marcenariapro
-- ------------------------------------------------------
-- Server version	8.4.11

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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ambientes`
--

LOCK TABLES `ambientes` WRITE;
/*!40000 ALTER TABLE `ambientes` DISABLE KEYS */;
INSERT INTO `ambientes` VALUES (1,'2026-09-12 18:20:43.793426','Cozinha planejada com armários superiores e inferiores','Cozinha',1),(2,'2026-09-12 18:39:22.632162','Sala com painel de TV e rack','Sala',1),(3,'2026-09-24 23:13:20.783484','Ambiente atualizado no teste','Cozinha Atualizada',2),(5,'2026-09-26 17:36:52.544542','Móveis planejados para o quarto','Quarto',2),(6,'2026-09-26 21:26:19.758204','Moveis planejados para o escritorio','Escritorio',2),(7,'2026-09-29 15:32:19.307910','','cozinha',4),(8,'2026-09-30 14:42:24.051823','banheiro suite','Banheiro',4),(9,'2026-09-30 17:50:40.098681','suite casal','quarto casal',4),(10,'2026-09-30 20:36:14.455804','Balcão pia , Aereo e Armario de Vassoura','Lavanderia',1),(11,'2026-10-04 19:30:20.860594','rack, painel de tv ','Sala ',4),(12,'2026-10-05 15:19:33.153991','Cama, Guarda Roupa, Painel de Tv','Quarto Solteiro',4),(13,'2026-10-18 22:25:51.947940','balcao pia e aereo','cozinha kitnet 1',5),(14,'2026-10-18 23:39:02.124478','novo','quarto',6);
/*!40000 ALTER TABLE `ambientes` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assistencias_tecnicas`
--

LOCK TABLES `assistencias_tecnicas` WRITE;
/*!40000 ALTER TABLE `assistencias_tecnicas` DISABLE KEYS */;
INSERT INTO `assistencias_tecnicas` VALUES (3,'2026-10-05 08:30:00.000000','2026-11-05','2026-10-05',_binary '','troca de corredica','ABERTA',195,0,195,3,2,2),(4,'2026-10-06 20:20:00.000000','2026-06-10','2026-10-06',_binary '','nada','ABERTA',0,0,0,2,2,NULL);
/*!40000 ALTER TABLE `assistencias_tecnicas` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'123.456.789-00','2026-08-09 20:48:07.832041','joao@gmail.com','Rua Nova, 300','João da Silva Atualizado 2','(45) 97777-7777'),(2,'111.222.333-44','2026-09-24 23:03:11.418003','maria@gmail.com','Rua Teste, 100','Maria Teste','45988887777'),(3,'78669885652','2026-09-28 15:45:43.256250','joais@gmail.com','Rua cabo fidelis de aguias 5252','josias','45998468525'),(4,'999.999.999-99','2026-10-06 20:42:44.330241','cliente@teste.com','Rua Teste, 100','Cliente Vendedor','45999999999'),(5,'11188899937','2026-10-18 22:24:17.022191','gilberto@gmail.com','Av Papaguaios 2235','Gilberto Volpato','45998745263'),(6,'11122233344','2026-10-18 23:10:44.678033','marias@gmail.com','Rua general osorio','Maria Teste','45988887777'),(7,'wuekshriweuri','2026-10-18 23:24:47.490528','wuesley@gmail.com','wuesley','12345649','wuesley'),(9,'52998224725','2026-10-21 19:56:22.182687','rrr@gmail.com','av brasil','Milena','45998752222');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `itens_assistencia`
--

LOCK TABLES `itens_assistencia` WRITE;
/*!40000 ALTER TABLE `itens_assistencia` DISABLE KEYS */;
/*!40000 ALTER TABLE `itens_assistencia` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itens_assistencia_tecnica`
--

LOCK TABLES `itens_assistencia_tecnica` WRITE;
/*!40000 ALTER TABLE `itens_assistencia_tecnica` DISABLE KEYS */;
INSERT INTO `itens_assistencia_tecnica` VALUES (2,1,3,2);
/*!40000 ALTER TABLE `itens_assistencia_tecnica` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itens_orcamento`
--

LOCK TABLES `itens_orcamento` WRITE;
/*!40000 ALTER TABLE `itens_orcamento` DISABLE KEYS */;
INSERT INTO `itens_orcamento` VALUES (2,4.00,'un',220.00,3,11),(3,3.00,'un',195.00,2,11),(4,15.00,'un',195.00,2,13),(5,3.00,'un',220.00,3,13),(6,1.00,'un',4.00,5,13),(7,2.00,'un',310.00,4,13),(8,2.00,'un',4.00,5,12),(9,10.00,'un',195.00,2,14),(10,5.00,'un',4.00,5,14),(11,6.00,'un',195.00,2,15),(12,10.00,'un',220.00,3,15);
/*!40000 ALTER TABLE `itens_orcamento` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itens_producao`
--

LOCK TABLES `itens_producao` WRITE;
/*!40000 ALTER TABLE `itens_producao` DISABLE KEYS */;
INSERT INTO `itens_producao` VALUES (1,5.00,2,2),(2,5.00,2,2),(3,1.00,2,6),(4,1.00,2,7),(5,2.00,2,8),(6,20.00,3,10),(7,50.00,2,10),(8,2.00,3,10),(10,1.00,2,11),(11,20.00,2,10),(12,2.00,3,11),(13,1.00,3,11),(14,1.00,3,10),(15,1.00,4,10),(16,15.00,2,12),(17,3.00,3,12),(18,1.00,5,12),(19,2.00,4,12),(20,2.00,5,13),(21,4.00,3,14),(22,3.00,2,14),(23,10.00,2,15),(24,5.00,5,15);
/*!40000 ALTER TABLE `itens_producao` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `materiais`
--

DROP TABLE IF EXISTS `materiais`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `materiais` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `descricao` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `quantidade` int DEFAULT NULL,
  `valor_unitario` decimal(38,2) DEFAULT NULL,
  `estoque_minimo` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `materiais`
--

LOCK TABLES `materiais` WRITE;
/*!40000 ALTER TABLE `materiais` DISABLE KEYS */;
INSERT INTO `materiais` VALUES (2,'Chapa de MDF branco para móveis planejados','MDF Branco 15mm',5,195.00,10),(3,'Chapa de MDF amadeirado para móveis','MDF Amadeirado 15mm',56,220.00,5),(4,'chapa 275*185','MDF Itapua 15 mm',0,310.00,0),(5,'','Dobradiça baixa normal ',42,4.00,15);
/*!40000 ALTER TABLE `materiais` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimentacoes_estoque`
--

LOCK TABLES `movimentacoes_estoque` WRITE;
/*!40000 ALTER TABLE `movimentacoes_estoque` DISABLE KEYS */;
INSERT INTO `movimentacoes_estoque` VALUES (1,'2026-09-13 12:49:48.598089',10,'ENTRADA',2),(2,'2026-09-13 12:50:52.505296',5,'SAIDA',2),(3,'2026-09-26 18:28:08.684044',1,'SAIDA',2),(4,'2026-09-26 18:28:36.282098',1,'ENTRADA',2),(5,'2026-09-26 20:02:33.549256',1,'SAIDA',2),(6,'2026-09-27 00:01:12.093779',1,'ENTRADA',2),(7,'2026-09-27 00:01:48.984932',2,'SAIDA',2),(8,'2026-09-30 16:45:53.683659',1,'SAIDA',2),(9,'2026-09-30 16:45:53.705293',2,'SAIDA',3),(10,'2026-09-30 16:46:42.519596',1,'SAIDA',3),(11,'2026-09-30 17:39:05.312927',1,'ENTRADA',2),(12,'2026-09-30 17:39:05.334523',2,'ENTRADA',3),(13,'2026-09-30 17:39:05.354210',1,'ENTRADA',3),(14,'2026-09-30 17:45:14.442953',1,'SAIDA',3),(15,'2026-09-30 17:49:46.847781',1,'SAIDA',4),(16,'2026-09-30 20:38:13.762961',30,'ENTRADA',2),(17,'2026-09-30 20:38:25.664523',50,'ENTRADA',3),(18,'2026-10-04 22:50:11.478889',15,'SAIDA',2),(19,'2026-10-04 22:50:11.503491',3,'SAIDA',3),(20,'2026-10-04 22:50:11.525873',1,'SAIDA',5),(21,'2026-10-04 22:51:32.985840',2,'ENTRADA',4),(22,'2026-10-04 22:52:02.798378',15,'SAIDA',2),(23,'2026-10-04 22:52:02.867721',3,'SAIDA',3),(24,'2026-10-04 22:52:02.904531',1,'SAIDA',5),(25,'2026-10-04 22:52:02.930339',2,'SAIDA',4),(26,'2026-10-04 22:58:20.864234',15,'ENTRADA',2),(27,'2026-10-04 22:58:40.977083',3,'ENTRADA',3),(28,'2026-10-04 22:59:20.934646',1,'ENTRADA',5),(29,'2026-10-04 23:08:08.776051',2,'SAIDA',5),(30,'2026-10-04 23:10:21.045405',14,'SAIDA',2),(31,'2026-10-04 23:12:01.428329',14,'ENTRADA',2),(32,'2026-10-05 21:15:56.904012',10,'SAIDA',2),(33,'2026-10-05 21:15:56.965982',5,'SAIDA',5);
/*!40000 ALTER TABLE `movimentacoes_estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orcamentos`
--

DROP TABLE IF EXISTS `orcamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orcamentos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_criacao` datetime(6) DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `status` enum('APROVADO','PENDENTE','RECUSADO') DEFAULT NULL,
  `valor` decimal(38,2) DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  `ambiente_id` bigint DEFAULT NULL,
  `data_validade` datetime(6) DEFAULT NULL,
  `margem_lucro` decimal(38,2) DEFAULT NULL,
  `valor_mao_obra` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKci1c52roidio1t2lruyc0gwb` (`ambiente_id`),
  KEY `FKcsjpjxc7xgdqqg51fe8gl1lh2` (`cliente_id`),
  CONSTRAINT `FKcsjpjxc7xgdqqg51fe8gl1lh2` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  CONSTRAINT `FKft6ylbkmuciluxw2ujfrodo9w` FOREIGN KEY (`ambiente_id`) REFERENCES `ambientes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orcamentos`
--

LOCK TABLES `orcamentos` WRITE;
/*!40000 ALTER TABLE `orcamentos` DISABLE KEYS */;
INSERT INTO `orcamentos` VALUES (2,NULL,'Orçamento dos móveis da cozinha','APROVADO',5000.00,NULL,1,NULL,NULL,NULL),(4,'2026-09-12 18:39:50.261871','Orçamento dos móveis da sala','APROVADO',3500.00,NULL,2,NULL,NULL,NULL),(5,'2026-09-24 23:18:13.443561','Orçamento atualizado da cozinha','APROVADO',2800.00,NULL,3,NULL,NULL,NULL),(7,'2026-09-26 17:37:02.127972','Orçamento dos móveis do quarto atualizado','APROVADO',4500.00,NULL,5,'2026-12-25 17:37:02.127972',NULL,NULL),(8,'2026-09-26 21:27:07.497810','Orcamento de teste de estoque','APROVADO',1000.00,NULL,6,'2026-12-25 21:27:07.497810',NULL,NULL),(9,'2026-09-29 15:32:47.948769','','APROVADO',6000.00,NULL,7,'2026-12-28 15:32:47.948769',NULL,NULL),(10,'2026-09-30 14:42:44.229055','','APROVADO',4000.00,NULL,8,'2026-12-29 14:42:44.229055',NULL,NULL),(11,'2026-09-30 17:51:18.561007','guarda-roupa, cabeçeira e painel tv','APROVADO',5804.50,NULL,9,'2026-12-29 17:51:18.561007',30.00,3000.00),(12,'2026-09-30 20:36:55.094341','','APROVADO',3910.40,NULL,10,'2026-12-29 20:36:55.094341',30.00,3000.00),(13,'2026-10-04 19:31:02.761907','atual','APROVADO',8071.70,NULL,11,'2027-01-02 19:31:02.761907',30.00,2000.00),(14,'2026-10-05 15:20:07.632601','Novo','APROVADO',5970.00,NULL,12,'2027-01-03 15:20:07.632601',0.00,4000.00),(15,'2026-10-18 22:26:12.205175','novo','APROVADO',5681.00,NULL,13,'2027-01-16 22:26:12.205175',30.00,1000.00),(16,'2026-10-18 23:57:15.169040','','PENDENTE',1300.00,NULL,14,'2027-01-16 23:57:15.169040',30.00,1000.00);
/*!40000 ALTER TABLE `orcamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagamentos`
--

DROP TABLE IF EXISTS `pagamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagamentos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_pagamento` datetime(6) DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `status` enum('CANCELADO','PAGO','PENDENTE') DEFAULT NULL,
  `valor` decimal(38,2) DEFAULT NULL,
  `pedido_id` bigint DEFAULT NULL,
  `caminho_comprovante` varchar(255) DEFAULT NULL,
  `nome_comprovante` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb0dh6865y8h4irwdrl4ici3kl` (`pedido_id`),
  CONSTRAINT `FKb0dh6865y8h4irwdrl4ici3kl` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagamentos`
--

LOCK TABLES `pagamentos` WRITE;
/*!40000 ALTER TABLE `pagamentos` DISABLE KEYS */;
INSERT INTO `pagamentos` VALUES (1,'2026-09-20 18:00:00.000000','Sinal de 30% do pedido','PAGO',1050.00,2,NULL,NULL),(2,'2026-09-20 19:00:00.000000','Pagamento final de 70%','PAGO',2450.00,2,NULL,NULL),(3,'2026-09-20 19:00:00.000000','Sinal de 30% do pedido','PAGO',1500.00,3,NULL,NULL),(4,'2026-09-20 20:30:00.000000','Pagamento final de 70%','PAGO',3500.00,3,NULL,NULL),(5,NULL,'Entrada de 30% do pedido','PAGO',840.00,5,NULL,NULL),(6,NULL,'Pagamento final após entrega','PAGO',1960.00,5,NULL,NULL),(7,NULL,NULL,'PAGO',1350.00,4,NULL,NULL),(8,NULL,NULL,'PAGO',300.00,6,NULL,NULL),(9,'2026-09-27 00:09:28.315911','Pagamento final','PAGO',700.00,6,NULL,NULL),(10,'2026-09-29 19:27:00.000000','Entrada','PAGO',2000.00,7,NULL,NULL),(11,'2026-09-30 16:40:00.000000','Entrada de 30%','PAGO',1200.00,8,NULL,NULL),(12,'2026-10-04 22:50:00.000000','Sinal de 30% do pedido','PAGO',2421.51,9,NULL,NULL),(13,'2026-10-04 23:10:00.000000','Sinal de 30% do pedido de teste','PAGO',1173.12,10,NULL,NULL),(14,'2026-10-04 23:12:00.000000','Sinal de 30% do pedido de teste de estoque','PAGO',1741.35,11,NULL,NULL),(15,'2026-10-05 23:37:00.000000','Entrada','PAGO',1791.00,12,NULL,NULL),(16,'2026-11-02 20:24:00.000000','Resto do pagamento ','PAGO',3150.00,4,NULL,NULL),(17,'2026-11-02 17:28:25.373111','','PAGO',200.00,8,NULL,NULL),(18,'2026-11-02 23:05:00.000000','Entrada','PAGO',1800.00,13,'/mnt/c/Users/wuesley/OneDrive/Imagens/Desktop/TCC/marcenariapro/comprovantes/cef25ba0-74a9-470a-b237-979bfbb49913.png','fundo-login.png');
/*!40000 ALTER TABLE `pagamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `data_criacao` datetime(6) DEFAULT NULL,
  `status` enum('CANCELADO','APROVADO','ENTREGUE','PENDENTE') DEFAULT NULL,
  `orcamento_id` bigint DEFAULT NULL,
  `data_entrega` date DEFAULT NULL,
  `prazo_entrega` date DEFAULT NULL,
  `previsao_entrega` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgdtlgtcourwx52rfjeexbpvrs` (`orcamento_id`),
  CONSTRAINT `FKme15slf87t5e4co2a3md0xmfh` FOREIGN KEY (`orcamento_id`) REFERENCES `orcamentos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
INSERT INTO `pedidos` VALUES (2,'2026-09-13 12:33:49.880110','ENTREGUE',4,NULL,NULL,NULL),(3,'2026-09-20 18:43:56.243259','ENTREGUE',2,NULL,NULL,NULL),(4,'2026-09-26 17:47:22.405479','APROVADO',7,NULL,NULL,NULL),(5,'2026-09-26 17:52:38.623394','ENTREGUE',5,NULL,NULL,NULL),(6,'2026-09-26 21:27:49.985046','ENTREGUE',8,NULL,NULL,NULL),(7,'2026-09-29 15:33:50.264949','PENDENTE',9,NULL,NULL,NULL),(8,'2026-09-30 14:44:26.912852','APROVADO',10,NULL,NULL,NULL),(9,'2026-10-04 22:45:05.417618','PENDENTE',13,NULL,NULL,NULL),(10,'2026-10-04 23:06:30.355840','PENDENTE',12,NULL,NULL,NULL),(11,'2026-10-04 23:09:17.960664','PENDENTE',11,NULL,NULL,NULL),(12,'2026-10-05 20:17:45.342136','APROVADO',14,NULL,NULL,'2026-10-06'),(13,'2026-10-18 22:46:14.950089','APROVADO',15,NULL,NULL,'2026-11-20');
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producoes`
--

LOCK TABLES `producoes` WRITE;
/*!40000 ALTER TABLE `producoes` DISABLE KEYS */;
INSERT INTO `producoes` VALUES (2,'2026-09-13 17:04:53.738444','ENTREGUE',2),(4,'2026-09-20 19:29:22.823320','ENTREGUE',3),(6,'2026-09-26 17:54:18.539188','ENTREGUE',5),(7,'2026-09-26 19:32:07.310642','CANCELADO',4),(8,'2026-09-26 21:28:05.949883','ENTREGUE',6),(10,'2026-09-29 16:29:38.426313','EM_PRODUCAO',7),(11,'2026-09-30 14:44:48.953625','CANCELADO',8),(12,'2026-10-04 22:45:39.772867','EM_PRODUCAO',9),(13,'2026-10-04 23:07:03.166797','EM_PRODUCAO',10),(14,'2026-10-04 23:09:30.088506','AGUARDANDO',11),(15,'2026-10-05 20:38:24.863397','EM_PRODUCAO',12);
/*!40000 ALTER TABLE `producoes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produtos`
--

DROP TABLE IF EXISTS `produtos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produtos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ativo` bit(1) DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `estoque` int DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `preco` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos`
--

LOCK TABLES `produtos` WRITE;
/*!40000 ALTER TABLE `produtos` DISABLE KEYS */;
INSERT INTO `produtos` VALUES (1,_binary '','Mesa de madeira maciça',5,'Mesa de Madeira',1500);
/*!40000 ALTER TABLE `produtos` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projetos`
--

LOCK TABLES `projetos` WRITE;
/*!40000 ALTER TABLE `projetos` DISABLE KEYS */;
INSERT INTO `projetos` VALUES (1,'2026-09-12 18:15:56.650805','Projeto de móveis planejados para cozinha','Cozinha Planejada',1),(2,'2026-09-24 23:08:10.481838','Descrição alterada no teste','Cozinha Planejada Teste',1),(4,'2026-09-29 15:31:56.741573','completo','apartamento',1),(5,'2026-10-18 22:24:55.873525','4 kitnets','Kit nets',5),(6,'2026-10-18 23:38:43.080377','Mansao em condominio','Mansao',7);
/*!40000 ALTER TABLE `projetos` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solicitacoes_substituicao`
--

LOCK TABLES `solicitacoes_substituicao` WRITE;
/*!40000 ALTER TABLE `solicitacoes_substituicao` DISABLE KEYS */;
INSERT INTO `solicitacoes_substituicao` VALUES (1,'APROVADA',3,2),(2,'RECUSADA',3,3);
/*!40000 ALTER TABLE `solicitacoes_substituicao` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,_binary '','2026-08-09 19:43:02.620928','admin@marcenariapro.com','Administrador','ADMIN','$2a$10$gkIK/Kb8uGiUguD32B5WO.UuS9yBoE5kRgEi1YzLGoSXDIVJe2.Yq'),(2,_binary '','2026-08-09 19:58:38.732167','joao@marcenariapro.com','João','MARCENEIRO','$2a$10$lVyeHw9u9/ZBugF29BgGsOUmk9FSC75mcVH4RAseXHFP80CNuM0Ue'),(3,_binary '','2026-10-06 20:07:09.017380','vendedor@marcenariapro.com','Vendedor Teste','VENDEDOR','$2a$10$CV7bAoftbzP3nAR/uCL4RuxgmPxXmI.IEwIhvs14EubQiBCWbhP4i'),(4,_binary '','2026-10-06 20:48:08.729473','marceneiro@marcenariapro.com','Marceneiro Teste','MARCENEIRO','$2a$10$dyz02j6RwVWT0GzxPwfvHOPrpxsCLWFQnJqkBJOnDB3sY5br9oC.K');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'marcenariapro'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-03  1:55:50
