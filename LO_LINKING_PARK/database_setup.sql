-- ================================================================
-- SCRIPT SQL COMPLETO PARA PARKING LA SALLE
-- Copiar y pegar en phpMyAdmin > pestaña SQL
-- ================================================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS parking_lasalle
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE parking_lasalle;

-- ================================================================
-- TABLA: usuaris
-- ================================================================
CREATE TABLE IF NOT EXISTS usuaris (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    cognoms VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefon VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('admin', 'usuari') NOT NULL DEFAULT 'usuari',
    actiu TINYINT(1) NOT NULL DEFAULT 1,
    creat_el DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualitzat_el DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_actiu (actiu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLA: salles
-- ================================================================
CREATE TABLE IF NOT EXISTS salles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    ciutat VARCHAR(100) NOT NULL,
    adreca VARCHAR(255),
    latitud DECIMAL(10, 8) NOT NULL,
    longitud DECIMAL(11, 8) NOT NULL,
    places_totals INT NOT NULL DEFAULT 0,
    places_disponibles INT NOT NULL DEFAULT 0,
    actiu TINYINT(1) NOT NULL DEFAULT 1,
    creat_el DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ciutat (ciutat),
    INDEX idx_actiu (actiu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLA: vehicles
-- ================================================================
CREATE TABLE IF NOT EXISTS vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuari_id INT NOT NULL,
    matricula VARCHAR(20) NOT NULL,
    marca VARCHAR(100),
    model VARCHAR(100),
    color VARCHAR(50),
    any_fabricacio INT,
    actiu TINYINT(1) NOT NULL DEFAULT 1,
    creat_el DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuari_id) REFERENCES usuaris(id) ON DELETE CASCADE,
    INDEX idx_usuari (usuari_id),
    INDEX idx_actiu (actiu),
    INDEX idx_matricula (matricula)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLA: sessions
-- ================================================================
CREATE TABLE IF NOT EXISTS sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuari_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    salle_id INT,
    data_inici DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fi DATETIME,
    temps_maxim_minuts INT NOT NULL DEFAULT 60,
    aviso_temps_minuts INT NOT NULL DEFAULT 10,
    tipus_finalitzacio ENUM('manual', 'temps', 'admin') DEFAULT NULL,
    latitud_inici DECIMAL(10, 8),
    longitud_inici DECIMAL(11, 8),
    estat ENUM('actiu', 'finalitzat', 'cancel·lat') NOT NULL DEFAULT 'actiu',
    creat_el DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuari_id) REFERENCES usuaris(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE,
    FOREIGN KEY (salle_id) REFERENCES salles(id) ON DELETE SET NULL,
    INDEX idx_usuari (usuari_id),
    INDEX idx_vehicle (vehicle_id),
    INDEX idx_estat (estat),
    INDEX idx_data_inici (data_inici)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- DATOS INICIALES: Salles de La Salle Catalunya
-- ================================================================
INSERT INTO salles (nom, ciutat, adreca, latitud, longitud, places_totals, places_disponibles) VALUES
('La Salle Mollerussa', 'Mollerussa', 'Carrer de la Industria, 12, 25230 Mollerussa', 41.61320000, 0.62450000, 50, 50),
('La Salle Campus Barcelona', 'Barcelona', 'Carrer de Sant Joan de la Salle, 42, 08022 Barcelona', 41.41560000, 2.17450000, 100, 100),
('La Salle Bonanova', 'Barcelona', 'Passeig de la Bonanova, 8, 08022 Barcelona', 41.40890000, 2.13640000, 75, 75),
('La Salle Tarragona', 'Tarragona', 'Carrer de la Salle, 1, 43001 Tarragona', 41.11890000, 1.24450000, 60, 60),
('La Salle Girona', 'Girona', 'Avinguda de Montilivi, 16, 17003 Girona', 41.97940000, 2.82140000, 80, 80);

-- ================================================================
-- USUARIO ADMIN POR DEFECTO
-- Email: admin@lasalle.cat
-- Password: admin123
-- ================================================================
INSERT INTO usuaris (nom, cognoms, email, telefon, password_hash, rol) VALUES
('Admin', 'La Salle', 'admin@lasalle.cat', '000000000',
 '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin');

-- ================================================================
-- VERIFICACIÓN
-- ================================================================
SELECT 'Base de datos creada correctamente' AS mensaje;
SELECT COUNT(*) AS total_salles FROM salles;
SELECT COUNT(*) AS total_usuarios FROM usuaris;
SELECT 'Usuario admin creado: admin@lasalle.cat / admin123' AS info;

