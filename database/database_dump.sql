-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 14, 2025 at 03:49 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gestion_commandes_factures`
--

-- --------------------------------------------------------

--
-- Table structure for table `clients`
--

CREATE TABLE `clients` (
  `idClient` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `telephone` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `commandes`
--

CREATE TABLE `commandes` (
  `idCommande` int(11) NOT NULL,
  `date` datetime NOT NULL,
  `idClient` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `factures`
--

CREATE TABLE `factures` (
  `idFacture` int(11) NOT NULL,
  `date` datetime NOT NULL,
  `montantTotal` double NOT NULL,
  `idClient` int(11) DEFAULT NULL,
  `discount` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lignes_commande`
--

CREATE TABLE `lignes_commande` (
  `idLigneCommande` int(11) NOT NULL,
  `idCommande` int(11) DEFAULT NULL,
  `idProduit` int(11) DEFAULT NULL,
  `quantite` int(11) NOT NULL,
  `sousTotal` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lignes_facture`
--

CREATE TABLE `lignes_facture` (
  `idLigne` int(11) NOT NULL,
  `idFacture` int(11) DEFAULT NULL,
  `idProduit` int(11) DEFAULT NULL,
  `quantite` int(11) NOT NULL,
  `sousTotal` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `produits`
--

CREATE TABLE `produits` (
  `idProduit` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `prix` double NOT NULL,
  `quantiteEnStock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `produits`
--

INSERT INTO `produits` (`idProduit`, `nom`, `prix`, `quantiteEnStock`) VALUES
(5, 'kajo', 2000, 11);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `clients`
--
ALTER TABLE `clients`
  ADD PRIMARY KEY (`idClient`);

--
-- Indexes for table `commandes`
--
ALTER TABLE `commandes`
  ADD PRIMARY KEY (`idCommande`),
  ADD KEY `idClient` (`idClient`);

--
-- Indexes for table `factures`
--
ALTER TABLE `factures`
  ADD PRIMARY KEY (`idFacture`),
  ADD KEY `idClient` (`idClient`);

--
-- Indexes for table `lignes_commande`
--
ALTER TABLE `lignes_commande`
  ADD PRIMARY KEY (`idLigneCommande`),
  ADD KEY `idCommande` (`idCommande`),
  ADD KEY `idProduit` (`idProduit`);

--
-- Indexes for table `lignes_facture`
--
ALTER TABLE `lignes_facture`
  ADD PRIMARY KEY (`idLigne`),
  ADD KEY `idFacture` (`idFacture`),
  ADD KEY `idProduit` (`idProduit`);

--
-- Indexes for table `produits`
--
ALTER TABLE `produits`
  ADD PRIMARY KEY (`idProduit`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `clients`
--
ALTER TABLE `clients`
  MODIFY `idClient` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `commandes`
--
ALTER TABLE `commandes`
  MODIFY `idCommande` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `factures`
--
ALTER TABLE `factures`
  MODIFY `idFacture` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `lignes_commande`
--
ALTER TABLE `lignes_commande`
  MODIFY `idLigneCommande` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `lignes_facture`
--
ALTER TABLE `lignes_facture`
  MODIFY `idLigne` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `produits`
--
ALTER TABLE `produits`
  MODIFY `idProduit` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `commandes`
--
ALTER TABLE `commandes`
  ADD CONSTRAINT `commandes_ibfk_1` FOREIGN KEY (`idClient`) REFERENCES `clients` (`idClient`);

--
-- Constraints for table `factures`
--
ALTER TABLE `factures`
  ADD CONSTRAINT `factures_ibfk_1` FOREIGN KEY (`idClient`) REFERENCES `clients` (`idClient`);

--
-- Constraints for table `lignes_commande`
--
ALTER TABLE `lignes_commande`
  ADD CONSTRAINT `lignes_commande_ibfk_1` FOREIGN KEY (`idCommande`) REFERENCES `commandes` (`idCommande`),
  ADD CONSTRAINT `lignes_commande_ibfk_2` FOREIGN KEY (`idProduit`) REFERENCES `produits` (`idProduit`);

--
-- Constraints for table `lignes_facture`
--
ALTER TABLE `lignes_facture`
  ADD CONSTRAINT `lignes_facture_ibfk_1` FOREIGN KEY (`idFacture`) REFERENCES `factures` (`idFacture`),
  ADD CONSTRAINT `lignes_facture_ibfk_2` FOREIGN KEY (`idProduit`) REFERENCES `produits` (`idProduit`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
