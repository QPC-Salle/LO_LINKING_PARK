-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 20-01-2026 a las 09:07:12
-- Versión del servidor: 9.1.0
-- Versión de PHP: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `parkimetre_schema`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `configuracio`
--

DROP TABLE IF EXISTS `configuracio`;
CREATE TABLE IF NOT EXISTS `configuracio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `clau` varchar(50) NOT NULL,
  `valor` varchar(255) NOT NULL,
  `descripcio` text,
  `tipus_dada` enum('int','decimal','string','boolean') DEFAULT 'string',
  `modificable_per` enum('admin','sistema') DEFAULT 'admin',
  `actualitzat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `clau` (`clau`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `configuracio`
--

INSERT INTO `configuracio` (`id`, `clau`, `valor`, `descripcio`, `tipus_dada`, `modificable_per`, `actualitzat_el`) VALUES
(1, 'tarifa_per_hora', '2.50', 'Tarifa per hora en euros', 'decimal', 'admin', '2026-01-16 09:45:36'),
(2, 'temps_maxim_default', '120', 'Temps màxim per defecte en minuts', 'int', 'admin', '2026-01-16 09:45:36'),
(3, 'temps_aviso_default', '15', 'Temps d\'avís per defecte abans de finalitzar (minuts)', 'int', 'admin', '2026-01-16 09:45:36'),
(4, 'max_vehicles_per_usuari', '5', 'Nombre màxim de vehicles per usuari', 'int', 'sistema', '2026-01-16 09:45:36'),
(5, 'radi_localitzacio_metres', '100', 'Radi de proximitat per activar parquímetre (metres)', 'int', 'admin', '2026-01-16 09:45:36');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `dades_bancaries`
--

DROP TABLE IF EXISTS `dades_bancaries`;
CREATE TABLE IF NOT EXISTS `dades_bancaries` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuari_id` int NOT NULL,
  `nom_titular` varchar(100) NOT NULL,
  `numero_compte` varchar(24) NOT NULL,
  `tipus_targeta` enum('credit','debit') DEFAULT 'debit',
  `ultims_digits` varchar(4) DEFAULT NULL,
  `token_pagament` varchar(255) DEFAULT NULL,
  `predeterminat` tinyint(1) DEFAULT '0',
  `actiu` tinyint(1) DEFAULT '1',
  `creat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `actualitzat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `usuari_id` (`usuari_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura Stand-in para la vista `historic_sessions`
-- (Véase abajo para la vista actual)
--
DROP VIEW IF EXISTS `historic_sessions`;
CREATE TABLE IF NOT EXISTS `historic_sessions` (
`data_fi` timestamp
,`data_inici` timestamp
,`data_pagament` timestamp
,`estat` enum('actiu','finalitzat','cancel·lat')
,`estat_pagament` enum('pendent','processat','completat','fallat','reemborsat')
,`import_total` decimal(10,2)
,`matricula` varchar(15)
,`salle_ciutat` varchar(100)
,`salle_nom` varchar(100)
,`sessio_id` int
,`temps_maxim_minuts` int
,`temps_real_minuts` bigint
,`tipus_finalitzacio` enum('manual','temps','admin')
,`usuari_cognoms` varchar(100)
,`usuari_email` varchar(150)
,`usuari_nom` varchar(100)
,`vehicle_marca` varchar(50)
,`vehicle_model` varchar(50)
);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `notificacions`
--

DROP TABLE IF EXISTS `notificacions`;
CREATE TABLE IF NOT EXISTS `notificacions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuari_id` int NOT NULL,
  `sessio_id` int DEFAULT NULL,
  `tipus` enum('aviso_temps','finalitzacio','pagament','sistema') NOT NULL,
  `titol` varchar(100) NOT NULL,
  `missatge` text NOT NULL,
  `llegida` tinyint(1) DEFAULT '0',
  `enviada` tinyint(1) DEFAULT '0',
  `data_enviament` timestamp NULL DEFAULT NULL,
  `creat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `sessio_id` (`sessio_id`),
  KEY `idx_notificacio_usuari` (`usuari_id`),
  KEY `idx_notificacio_llegida` (`llegida`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pagaments`
--

DROP TABLE IF EXISTS `pagaments`;
CREATE TABLE IF NOT EXISTS `pagaments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sessio_id` int NOT NULL,
  `usuari_id` int NOT NULL,
  `dades_bancaries_id` int NOT NULL,
  `temps_total_minuts` int NOT NULL,
  `tarifa_per_hora` decimal(10,2) NOT NULL,
  `import_total` decimal(10,2) NOT NULL,
  `estat_pagament` enum('pendent','processat','completat','fallat','reemborsat') DEFAULT 'pendent',
  `data_pagament` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `referencia_transaccio` varchar(100) DEFAULT NULL,
  `notes` text,
  `creat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `actualitzat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `dades_bancaries_id` (`dades_bancaries_id`),
  KEY `idx_pagament_sessio` (`sessio_id`),
  KEY `idx_pagament_usuari` (`usuari_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `salles`
--

DROP TABLE IF EXISTS `salles`;
CREATE TABLE IF NOT EXISTS `salles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `ciutat` varchar(100) NOT NULL DEFAULT 'Catalunya',
  `adreca` varchar(255) NOT NULL,
  `latitud` decimal(10,8) DEFAULT NULL,
  `longitud` decimal(11,8) DEFAULT NULL,
  `places_totals` int NOT NULL,
  `places_disponibles` int NOT NULL,
  `actiu` tinyint(1) DEFAULT '1',
  `creat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `actualitzat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `salles`
--

INSERT INTO `salles` (`id`, `nom`, `ciutat`, `adreca`, `latitud`, `longitud`, `places_totals`, `places_disponibles`, `actiu`, `creat_el`, `actualitzat_el`) VALUES
(1, 'La Salle Campus Barcelona', 'Barcelona', 'Carrer de Sant Joan de la Salle, 42, 08022 Barcelona', 41.41560000, 2.17450000, 100, 85, 1, '2026-01-16 09:45:36', '2026-01-16 09:45:36'),
(2, 'La Salle Bonanova', 'Barcelona', 'Passeig de la Bonanova, 8, 08022 Barcelona', 41.40890000, 2.13640000, 75, 60, 1, '2026-01-16 09:45:36', '2026-01-16 09:45:36'),
(3, 'La Salle Gràcia', 'Barcelona', 'Carrer de Girona, 24-26, 08010 Barcelona', 41.39420000, 2.16560000, 50, 42, 1, '2026-01-16 09:45:36', '2026-01-16 09:45:36'),
(4, 'La Salle Tarragona', 'Tarragona', 'Carrer de la Salle, 1, 43001 Tarragona', 41.11890000, 1.24450000, 60, 55, 1, '2026-01-16 09:45:36', '2026-01-16 09:45:36'),
(5, 'La Salle Girona', 'Girona', 'Avinguda de Montilivi, 16, 17003 Girona', 41.97940000, 2.82140000, 80, 70, 1, '2026-01-16 09:45:36', '2026-01-16 09:45:36');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sessions_parkimetre`
--

DROP TABLE IF EXISTS `sessions_parkimetre`;
CREATE TABLE IF NOT EXISTS `sessions_parkimetre` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuari_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `salle_id` int NOT NULL,
  `data_inici` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `data_fi` timestamp NULL DEFAULT NULL,
  `temps_maxim_minuts` int NOT NULL,
  `aviso_temps_minuts` int NOT NULL,
  `tipus_finalitzacio` enum('manual','temps','admin') DEFAULT NULL,
  `latitud_inici` decimal(10,8) DEFAULT NULL,
  `longitud_inici` decimal(11,8) DEFAULT NULL,
  `estat` enum('actiu','finalitzat','cancel·lat') DEFAULT 'actiu',
  `aviso_enviat` tinyint(1) DEFAULT '0',
  `aviso_final_enviat` tinyint(1) DEFAULT '0',
  `creat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `actualitzat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `vehicle_id` (`vehicle_id`),
  KEY `salle_id` (`salle_id`),
  KEY `idx_sessio_usuari` (`usuari_id`),
  KEY `idx_sessio_estat` (`estat`),
  KEY `idx_sessio_data_inici` (`data_inici`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Disparadores `sessions_parkimetre`
--
DROP TRIGGER IF EXISTS `actualitzar_places_fi`;
DELIMITER $$
CREATE TRIGGER `actualitzar_places_fi` AFTER UPDATE ON `sessions_parkimetre` FOR EACH ROW BEGIN
    IF OLD.estat = 'actiu' AND NEW.estat IN ('finalitzat', 'cancel·lat') THEN
        UPDATE salles 
        SET places_disponibles = places_disponibles + 1 
        WHERE id = NEW.salle_id AND places_disponibles < places_totals;
    END IF;
END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `actualitzar_places_inici`;
DELIMITER $$
CREATE TRIGGER `actualitzar_places_inici` AFTER INSERT ON `sessions_parkimetre` FOR EACH ROW BEGIN
    IF NEW.estat = 'actiu' THEN
        UPDATE salles 
        SET places_disponibles = places_disponibles - 1 
        WHERE id = NEW.salle_id AND places_disponibles > 0;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuaris`
--

DROP TABLE IF EXISTS `usuaris`;
CREATE TABLE IF NOT EXISTS `usuaris` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `cognoms` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `telefon` varchar(20) DEFAULT NULL,
  `contrasenya_hash` varchar(255) NOT NULL,
  `rol` enum('admin','usuari') DEFAULT 'usuari',
  `actiu` tinyint(1) DEFAULT '1',
  `creat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `actualitzat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_usuari_email` (`email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
CREATE TABLE IF NOT EXISTS `vehicles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuari_id` int NOT NULL,
  `matricula` varchar(15) NOT NULL,
  `marca` varchar(50) NOT NULL,
  `model` varchar(50) NOT NULL,
  `color` varchar(30) DEFAULT NULL,
  `any_fabricacio` year DEFAULT NULL,
  `predeterminat` tinyint(1) DEFAULT '0',
  `actiu` tinyint(1) DEFAULT '1',
  `creat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `actualitzat_el` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `matricula` (`matricula`),
  KEY `idx_vehicle_matricula` (`matricula`),
  KEY `idx_vehicle_usuari` (`usuari_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Disparadores `vehicles`
--
DROP TRIGGER IF EXISTS `check_max_vehicles_before_insert`;
DELIMITER $$
CREATE TRIGGER `check_max_vehicles_before_insert` BEFORE INSERT ON `vehicles` FOR EACH ROW BEGIN
    DECLARE vehicle_count INT;
    
    SELECT COUNT(*) INTO vehicle_count
    FROM vehicles
    WHERE usuari_id = NEW.usuari_id AND actiu = TRUE;
    
    IF vehicle_count >= 5 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Un usuari no pot tenir més de 5 vehicles actius';
    END IF;
END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `check_max_vehicles_before_update`;
DELIMITER $$
CREATE TRIGGER `check_max_vehicles_before_update` BEFORE UPDATE ON `vehicles` FOR EACH ROW BEGIN
    DECLARE vehicle_count INT;
    
    IF NEW.actiu = TRUE AND OLD.actiu = FALSE THEN
        SELECT COUNT(*) INTO vehicle_count
        FROM vehicles
        WHERE usuari_id = NEW. usuari_id AND actiu = TRUE;
        
        IF vehicle_count >= 5 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Un usuari no pot tenir més de 5 vehicles actius';
        END IF;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura para la vista `historic_sessions`
--
DROP TABLE IF EXISTS `historic_sessions`;

DROP VIEW IF EXISTS `historic_sessions`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `historic_sessions`  AS SELECT `s`.`id` AS `sessio_id`, `u`.`nom` AS `usuari_nom`, `u`.`cognoms` AS `usuari_cognoms`, `u`.`email` AS `usuari_email`, `v`.`matricula` AS `matricula`, `v`.`marca` AS `vehicle_marca`, `v`.`model` AS `vehicle_model`, `sa`.`nom` AS `salle_nom`, `sa`.`ciutat` AS `salle_ciutat`, `s`.`data_inici` AS `data_inici`, `s`.`data_fi` AS `data_fi`, `s`.`temps_maxim_minuts` AS `temps_maxim_minuts`, timestampdiff(MINUTE,`s`.`data_inici`,coalesce(`s`.`data_fi`,now())) AS `temps_real_minuts`, `s`.`tipus_finalitzacio` AS `tipus_finalitzacio`, `s`.`estat` AS `estat`, `p`.`import_total` AS `import_total`, `p`.`estat_pagament` AS `estat_pagament`, `p`.`data_pagament` AS `data_pagament` FROM ((((`sessions_parkimetre` `s` join `usuaris` `u` on((`s`.`usuari_id` = `u`.`id`))) join `vehicles` `v` on((`s`.`vehicle_id` = `v`.`id`))) join `salles` `sa` on((`s`.`salle_id` = `sa`.`id`))) left join `pagaments` `p` on((`s`.`id` = `p`.`sessio_id`))) WHERE (`s`.`estat` in ('finalitzat','cancel·lat')) ORDER BY `s`.`data_fi` DESC ;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
