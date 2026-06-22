-- ============================================================
-- CineFlow — Script de configuración completo
-- Ejecutar en orden: esquema → procedimientos → datos
-- ============================================================

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS,   UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- ============================================================
-- 1. ESQUEMA
-- ============================================================
DROP SCHEMA IF EXISTS cineflow;
CREATE SCHEMA cineflow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE cineflow;

-- Catálogos sin FK
CREATE TABLE ESTADO_ASIENTO (
    id_estado_asiento TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_estado_asiento),
    UNIQUE KEY uq_estado_asiento_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE FORMATO_PROYECCION (
    id_formato_proyeccion TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre                VARCHAR(80) NOT NULL,
    PRIMARY KEY (id_formato_proyeccion),
    UNIQUE KEY uq_formato_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE ESTADO_PAGO (
    id_estado_pago TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_estado_pago),
    UNIQUE KEY uq_estado_pago_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE estado_reserva (
    id_estado_reserva TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_estado_reserva),
    UNIQUE KEY uq_estado_reserva_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE METODO_PAGO (
    id_metodo_pago TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(40) NOT NULL,
    PRIMARY KEY (id_metodo_pago),
    UNIQUE KEY uq_metodo_pago_nombre (nombre)
) ENGINE=InnoDB;

-- Entidades principales
CREATE TABLE USUARIO (
    idUsuario       INT UNSIGNED NOT NULL AUTO_INCREMENT,
    email           VARCHAR(120) NOT NULL,
    contrasenia     VARCHAR(255) NOT NULL,
    nombre          VARCHAR(80) NOT NULL,
    apellidos       VARCHAR(120) NOT NULL,
    telefono        VARCHAR(20) NULL,
    fechaNacimiento DATE NULL,
    fechaRegistro   DATETIME NOT NULL,
    PRIMARY KEY (idUsuario),
    UNIQUE KEY uq_usuario_email (email)
) ENGINE=InnoDB;

CREATE TABLE PAGO (
    idPago  INT UNSIGNED NOT NULL AUTO_INCREMENT,
    monto   DECIMAL(10,2) NOT NULL,
    fecha   DATETIME NOT NULL,
    estado  VARCHAR(30) NOT NULL,
    metodo  VARCHAR(40) NOT NULL,
    PRIMARY KEY (idPago)
) ENGINE=InnoDB;

CREATE TABLE PELICULA (
    id               INT UNSIGNED NOT NULL AUTO_INCREMENT,
    titulo           VARCHAR(180) NOT NULL,
    duracion         INT NOT NULL,
    sinopsis         TEXT NULL,
    edad_restriccion VARCHAR(10) NULL,
    -- Se guarda el ordinal del enum Java: 0=TERROR 1=ACCION 2=CIENCIA_FICCION 3=COMEDIA 4=ANIMACION 5=DRAMA
    genero           INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE CINE (
    id_cine   INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(120) NOT NULL,
    ubicacion VARCHAR(200) NOT NULL,
    PRIMARY KEY (id_cine)
) ENGINE=InnoDB;

CREATE TABLE SALA (
    id_sala  INT UNSIGNED NOT NULL AUTO_INCREMENT,
    numero   INT NOT NULL,
    capacidad INT NOT NULL,
    id_cine  INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_sala),
    UNIQUE KEY uq_sala_numero_cine (id_cine, numero),
    CONSTRAINT fk_sala_cine FOREIGN KEY (id_cine) REFERENCES CINE (id_cine) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE FUNCION (
    id_funcion  INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fecha_hora  DATETIME NOT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    id_pelicula INT UNSIGNED NOT NULL,
    id_sala     INT UNSIGNED NOT NULL,
    id_formato  TINYINT UNSIGNED NOT NULL,
    PRIMARY KEY (id_funcion),
    CONSTRAINT fk_funcion_pelicula FOREIGN KEY (id_pelicula) REFERENCES PELICULA (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_funcion_sala     FOREIGN KEY (id_sala)     REFERENCES SALA (id_sala) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_funcion_formato  FOREIGN KEY (id_formato)  REFERENCES FORMATO_PROYECCION (id_formato_proyeccion) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE asiento (
    id_asiento        INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fila              CHAR(1) NOT NULL,
    numero            INT NOT NULL,
    id_estado_asiento TINYINT UNSIGNED NOT NULL,
    id_funcion        INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_asiento),
    UNIQUE KEY uq_asiento_funcion (id_funcion, fila, numero),
    CONSTRAINT fk_asiento_estado  FOREIGN KEY (id_estado_asiento) REFERENCES ESTADO_ASIENTO (id_estado_asiento) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_asiento_funcion FOREIGN KEY (id_funcion)        REFERENCES FUNCION (id_funcion) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE RESERVA (
    idReserva       INT UNSIGNED NOT NULL AUTO_INCREMENT,
    fechaReserva    DATETIME NOT NULL,
    fechaExpiracion DATETIME NULL,
    estado          VARCHAR(30) NOT NULL,
    totalFinal      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    fid_usuario     INT UNSIGNED NULL,
    fid_pago        INT UNSIGNED NULL,
    PRIMARY KEY (idReserva),
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (fid_usuario) REFERENCES USUARIO (idUsuario) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_reserva_pago    FOREIGN KEY (fid_pago)    REFERENCES PAGO (idPago) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE reserva_asiento (
    id_reserva INT UNSIGNED NOT NULL,
    id_asiento INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_reserva, id_asiento),
    CONSTRAINT fk_reserva_asiento_reserva FOREIGN KEY (id_reserva) REFERENCES RESERVA (idReserva) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_reserva_asiento_asiento FOREIGN KEY (id_asiento) REFERENCES asiento (id_asiento) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE entrada (
    id_entrada INT UNSIGNED NOT NULL AUTO_INCREMENT,
    tipo       VARCHAR(20) NOT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    id_reserva INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_entrada),
    CONSTRAINT fk_entrada_reserva FOREIGN KEY (id_reserva) REFERENCES RESERVA (idReserva) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE CARTELERA (
    id_cartelera INT UNSIGNED NOT NULL AUTO_INCREMENT,
    descripcion  VARCHAR(120) NULL,
    id_cine      INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_cartelera),
    CONSTRAINT fk_cartelera_cine FOREIGN KEY (id_cine) REFERENCES CINE (id_cine) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE CARTELERA_PELICULA (
    id_cartelera INT UNSIGNED NOT NULL,
    id_pelicula  INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_cartelera, id_pelicula),
    CONSTRAINT fk_cp_cartelera FOREIGN KEY (id_cartelera) REFERENCES CARTELERA (id_cartelera) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cp_pelicula  FOREIGN KEY (id_pelicula)  REFERENCES PELICULA (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE inventario_cine (
    id_inventario    INT UNSIGNED NOT NULL AUTO_INCREMENT,
    stock_actual     INT NOT NULL DEFAULT 0,
    stock_minimo     INT NOT NULL DEFAULT 0,
    ultima_reposicion DATETIME NULL,
    id_cine          INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_inventario),
    CONSTRAINT fk_inventario_cine FOREIGN KEY (id_cine) REFERENCES CINE (id_cine) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE CONFITERIA (
    id_item        INT UNSIGNED NOT NULL AUTO_INCREMENT,
    tipo           VARCHAR(30) NOT NULL,   -- equivale a "Categoria" en el modelo Java
    nombre         VARCHAR(120) NOT NULL,
    cantidad       INT NOT NULL DEFAULT 0,
    precio_unitario DECIMAL(10,2) NOT NULL,
    id_inventario  INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_item),
    CONSTRAINT fk_confiteria_inventario FOREIGN KEY (id_inventario) REFERENCES inventario_cine (id_inventario) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE RESERVA_CONFITERIA (
    id_reserva    INT UNSIGNED NOT NULL,
    id_item       INT UNSIGNED NOT NULL,
    cantidad      INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_reserva, id_item),
    CONSTRAINT fk_rc_reserva FOREIGN KEY (id_reserva) REFERENCES RESERVA (idReserva) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_rc_item    FOREIGN KEY (id_item)    REFERENCES CONFITERIA (id_item) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE COMPROBANTE_COMPRA (
    id_comprobante INT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo_qr      VARCHAR(255) NULL,
    monto_total    DECIMAL(10,2) NOT NULL,
    id_reserva     INT UNSIGNED NOT NULL,
    PRIMARY KEY (id_comprobante),
    CONSTRAINT fk_comprobante_reserva FOREIGN KEY (id_reserva) REFERENCES RESERVA (idReserva) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ============================================================
-- 2. STORED PROCEDURES
-- ============================================================

-- USUARIO
DROP PROCEDURE IF EXISTS insertarUsuario;
DROP PROCEDURE IF EXISTS modificarUsuario;
DROP PROCEDURE IF EXISTS eliminarUsuario;
DROP PROCEDURE IF EXISTS buscarUsuarioPorId;
DROP PROCEDURE IF EXISTS listarUsuarios;
DROP PROCEDURE IF EXISTS buscarUsuarioPorEmail;

DELIMITER //

CREATE PROCEDURE insertarUsuario(
    IN  p_email           VARCHAR(120),
    IN  p_contrasenia     VARCHAR(255),
    IN  p_nombre          VARCHAR(80),
    IN  p_apellidos       VARCHAR(120),
    IN  p_telefono        VARCHAR(20),
    IN  p_fechaNacimiento DATE,
    IN  p_fechaRegistro   DATETIME,
    OUT p_id              INT)
BEGIN
    INSERT INTO USUARIO (email, contrasenia, nombre, apellidos, telefono, fechaNacimiento, fechaRegistro)
    VALUES (p_email, p_contrasenia, p_nombre, p_apellidos, p_telefono, p_fechaNacimiento, p_fechaRegistro);
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificarUsuario(
    IN p_email           VARCHAR(120),
    IN p_contrasenia     VARCHAR(255),
    IN p_nombre          VARCHAR(80),
    IN p_apellidos       VARCHAR(120),
    IN p_telefono        VARCHAR(20),
    IN p_fechaNacimiento DATE,
    IN p_id              INT)
BEGIN
    UPDATE USUARIO
    SET email = p_email, contrasenia = p_contrasenia, nombre = p_nombre,
        apellidos = p_apellidos, telefono = p_telefono, fechaNacimiento = p_fechaNacimiento
    WHERE idUsuario = p_id;
END //

CREATE PROCEDURE eliminarUsuario(IN p_id INT)
BEGIN DELETE FROM USUARIO WHERE idUsuario = p_id; END //

CREATE PROCEDURE buscarUsuarioPorId(IN p_id INT)
BEGIN SELECT * FROM USUARIO WHERE idUsuario = p_id; END //

CREATE PROCEDURE listarUsuarios()
BEGIN SELECT * FROM USUARIO; END //

CREATE PROCEDURE buscarUsuarioPorEmail(IN p_email VARCHAR(120))
BEGIN SELECT * FROM USUARIO WHERE email = p_email; END //

DELIMITER ;

-- PELICULA
DROP PROCEDURE IF EXISTS insertarPelicula;
DROP PROCEDURE IF EXISTS modificarPelicula;
DROP PROCEDURE IF EXISTS eliminarPelicula;
DROP PROCEDURE IF EXISTS buscarPeliculaPorId;
DROP PROCEDURE IF EXISTS listarPeliculas;

DELIMITER //

CREATE PROCEDURE insertarPelicula(
    IN  p_titulo           VARCHAR(180),
    IN  p_duracion         INT,
    IN  p_sinopsis         TEXT,
    IN  p_edad_restriccion VARCHAR(10),
    IN  p_genero           INT,
    OUT p_id               INT)
BEGIN
    INSERT INTO PELICULA (titulo, duracion, sinopsis, edad_restriccion, genero)
    VALUES (p_titulo, p_duracion, p_sinopsis, p_edad_restriccion, p_genero);
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificarPelicula(
    IN p_titulo           VARCHAR(180),
    IN p_duracion         INT,
    IN p_sinopsis         TEXT,
    IN p_edad_restriccion VARCHAR(10),
    IN p_genero           INT,
    IN p_id               INT)
BEGIN
    UPDATE PELICULA
    SET titulo = p_titulo, duracion = p_duracion, sinopsis = p_sinopsis,
        edad_restriccion = p_edad_restriccion, genero = p_genero
    WHERE id = p_id;
END //

CREATE PROCEDURE eliminarPelicula(IN p_id INT)
BEGIN DELETE FROM PELICULA WHERE id = p_id; END //

CREATE PROCEDURE buscarPeliculaPorId(IN p_id INT)
BEGIN SELECT * FROM PELICULA WHERE id = p_id; END //

CREATE PROCEDURE listarPeliculas()
BEGIN SELECT * FROM PELICULA; END //

DELIMITER ;

-- PAGO
DROP PROCEDURE IF EXISTS insertarPago;
DROP PROCEDURE IF EXISTS modificarPago;
DROP PROCEDURE IF EXISTS eliminarPago;
DROP PROCEDURE IF EXISTS buscarPagoPorId;
DROP PROCEDURE IF EXISTS listarPagos;

DELIMITER //

CREATE PROCEDURE insertarPago(
    IN  p_monto  DECIMAL(10,2),
    IN  p_fecha  DATETIME,
    IN  p_estado VARCHAR(30),
    IN  p_metodo VARCHAR(40),
    OUT p_id     INT)
BEGIN
    INSERT INTO PAGO (monto, fecha, estado, metodo) VALUES (p_monto, p_fecha, p_estado, p_metodo);
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificarPago(
    IN p_estado VARCHAR(30),
    IN p_monto  DECIMAL(10,2),
    IN p_id     INT)
BEGIN UPDATE PAGO SET estado = p_estado, monto = p_monto WHERE idPago = p_id; END //

CREATE PROCEDURE eliminarPago(IN p_id INT)
BEGIN DELETE FROM PAGO WHERE idPago = p_id; END //

CREATE PROCEDURE buscarPagoPorId(IN p_id INT)
BEGIN SELECT * FROM PAGO WHERE idPago = p_id; END //

CREATE PROCEDURE listarPagos()
BEGIN SELECT * FROM PAGO; END //

DELIMITER ;

-- RESERVA
DROP PROCEDURE IF EXISTS insertarReserva;
DROP PROCEDURE IF EXISTS modificarReserva;
DROP PROCEDURE IF EXISTS eliminarReserva;
DROP PROCEDURE IF EXISTS buscarReservaPorId;
DROP PROCEDURE IF EXISTS listarReservas;

DELIMITER //

CREATE PROCEDURE insertarReserva(
    IN  p_fechaReserva    DATETIME,
    IN  p_fechaExpiracion DATETIME,
    IN  p_estado          VARCHAR(30),
    IN  p_totalFinal      DECIMAL(10,2),
    IN  p_fid_usuario     INT,
    IN  p_fid_pago        INT,
    OUT p_id              INT)
BEGIN
    INSERT INTO RESERVA (fechaReserva, fechaExpiracion, estado, totalFinal, fid_usuario, fid_pago)
    VALUES (p_fechaReserva, p_fechaExpiracion, p_estado, p_totalFinal, p_fid_usuario, p_fid_pago);
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificarReserva(
    IN p_fechaReserva    DATETIME,
    IN p_fechaExpiracion DATETIME,
    IN p_estado          VARCHAR(30),
    IN p_totalFinal      DECIMAL(10,2),
    IN p_fid_usuario     INT,
    IN p_fid_pago        INT,
    IN p_id              INT)
BEGIN
    UPDATE RESERVA
    SET fechaReserva = p_fechaReserva, fechaExpiracion = p_fechaExpiracion,
        estado = p_estado, totalFinal = p_totalFinal,
        fid_usuario = p_fid_usuario, fid_pago = p_fid_pago
    WHERE idReserva = p_id;
END //

CREATE PROCEDURE eliminarReserva(IN p_id INT)
BEGIN DELETE FROM RESERVA WHERE idReserva = p_id; END //

CREATE PROCEDURE buscarReservaPorId(IN p_id INT)
BEGIN SELECT * FROM RESERVA WHERE idReserva = p_id; END //

CREATE PROCEDURE listarReservas()
BEGIN SELECT * FROM RESERVA; END //

DELIMITER ;

-- COMPROBANTE_COMPRA
DROP PROCEDURE IF EXISTS insertarComprobante;
DROP PROCEDURE IF EXISTS eliminarComprobante;
DROP PROCEDURE IF EXISTS buscarComprobantePorId;
DROP PROCEDURE IF EXISTS buscarComprobantePorReserva;
DROP PROCEDURE IF EXISTS listarComprobantes;

DELIMITER //

CREATE PROCEDURE insertarComprobante(
    IN  p_codigo_qr   VARCHAR(255),
    IN  p_monto_total DECIMAL(10,2),
    IN  p_id_reserva  INT,
    OUT p_id          INT)
BEGIN
    INSERT INTO COMPROBANTE_COMPRA (codigo_qr, monto_total, id_reserva)
    VALUES (p_codigo_qr, p_monto_total, p_id_reserva);
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE eliminarComprobante(IN p_id INT)
BEGIN DELETE FROM COMPROBANTE_COMPRA WHERE id_comprobante = p_id; END //

CREATE PROCEDURE buscarComprobantePorId(IN p_id INT)
BEGIN SELECT * FROM COMPROBANTE_COMPRA WHERE id_comprobante = p_id; END //

CREATE PROCEDURE buscarComprobantePorReserva(IN p_id_reserva INT)
BEGIN SELECT * FROM COMPROBANTE_COMPRA WHERE id_reserva = p_id_reserva LIMIT 1; END //

CREATE PROCEDURE listarComprobantes()
BEGIN SELECT * FROM COMPROBANTE_COMPRA; END //

DELIMITER ;


-- ============================================================
-- 3. DATOS CATÁLOGO (deben coincidir con enums Java)
-- ============================================================

-- EstadoAsiento { DISPONIBLE=1, OCUPADO=2, RESERVADO=3 }
INSERT INTO ESTADO_ASIENTO (nombre) VALUES ('DISPONIBLE'), ('OCUPADO'), ('RESERVADO');

-- FormatoProyeccion { DosD=1, tresD=2, IMAX=3 }
INSERT INTO FORMATO_PROYECCION (nombre) VALUES ('DosD'), ('tresD'), ('IMAX');

-- EstadoPago
INSERT INTO ESTADO_PAGO (nombre) VALUES ('PENDIENTE'), ('PROCESANDO'), ('APROBADO'), ('FALLIDO');

-- EstadoReserva
INSERT INTO estado_reserva (nombre) VALUES ('PENDIENTE'), ('CONFIRMADA'), ('CANCELADA');

-- MetodoPago
INSERT INTO METODO_PAGO (nombre) VALUES ('TARJETA'), ('BILLETERA_DIGITAL');


-- ============================================================
-- 4. DATOS DE PRUEBA
-- ============================================================

-- CINES
INSERT INTO CINE (nombre, ubicacion) VALUES
    ('CineFlow Miraflores', 'Av. Larco 345, Miraflores, Lima'),
    ('CineFlow San Isidro',  'Calle Las Begonias 441, San Isidro, Lima');

-- SALAS (3 por cine)
INSERT INTO SALA (numero, capacidad, id_cine) VALUES
    (1, 30, 1), (2, 40, 1), (3, 25, 1),
    (1, 35, 2), (2, 30, 2);

-- PELICULAS
-- genero: 0=TERROR 1=ACCION 2=CIENCIA_FICCION 3=COMEDIA 4=ANIMACION 5=DRAMA
INSERT INTO PELICULA (titulo, duracion, sinopsis, edad_restriccion, genero) VALUES
    ('Avengers: Endgame',      182, 'Los héroes restantes se unen para revertir el chasquido de Thanos.',       '+13', 1),
    ('Inception',              148, 'Un ladrón que roba secretos entrando en los sueños de sus víctimas.',      '+13', 2),
    ('La La Land',             128, 'Un músico de jazz y una actriz aspirante se enamoran en Los Ángeles.',     'ATP', 5),
    ('It: Capítulo 1',         135, 'Un grupo de niños enfrenta sus miedos contra un ente maligno.',            '+16', 0),
    ('El Rey León',             88, 'Simba debe reclamar su lugar como rey del reino de las bestias.',          'ATP', 4);

-- FUNCIONES
-- id_formato: 1=2D, 2=3D, 3=IMAX
INSERT INTO FUNCION (id_pelicula, id_sala, fecha_hora, precio_base, id_formato) VALUES
    (1, 1, '2026-06-22 14:00:00', 18.00, 1),  -- Avengers sala 1 cine 1 (2D)
    (1, 2, '2026-06-22 17:30:00', 22.00, 2),  -- Avengers sala 2 cine 1 (3D)
    (2, 1, '2026-06-22 20:00:00', 20.00, 1),  -- Inception sala 1 cine 1 (2D)
    (3, 3, '2026-06-23 19:00:00', 15.00, 1),  -- La La Land sala 3 cine 1 (2D)
    (4, 4, '2026-06-22 21:00:00', 22.00, 2),  -- It sala 1 cine 2 (3D)
    (5, 5, '2026-06-23 11:00:00', 16.00, 1);  -- Rey León sala 2 cine 2 (2D)

-- ASIENTOS
-- id_estado_asiento: 1=DISPONIBLE, 2=OCUPADO, 3=RESERVADO
-- Función 1 (Avengers 2D) — filas A-E, 6 asientos por fila
INSERT INTO asiento (fila, numero, id_estado_asiento, id_funcion) VALUES
('A',1,1,1),('A',2,1,1),('A',3,1,1),('A',4,1,1),('A',5,1,1),('A',6,1,1),
('B',1,1,1),('B',2,2,1),('B',3,2,1),('B',4,1,1),('B',5,1,1),('B',6,1,1),
('C',1,1,1),('C',2,1,1),('C',3,1,1),('C',4,1,1),('C',5,2,1),('C',6,1,1),
('D',1,1,1),('D',2,1,1),('D',3,1,1),('D',4,1,1),('D',5,1,1),('D',6,1,1),
('E',1,1,1),('E',2,1,1),('E',3,1,1),('E',4,1,1),('E',5,1,1),('E',6,1,1);

-- Función 2 (Avengers 3D) — filas A-D, 5 asientos
INSERT INTO asiento (fila, numero, id_estado_asiento, id_funcion) VALUES
('A',1,2,2),('A',2,2,2),('A',3,1,2),('A',4,1,2),('A',5,1,2),
('B',1,1,2),('B',2,1,2),('B',3,2,2),('B',4,1,2),('B',5,1,2),
('C',1,1,2),('C',2,1,2),('C',3,1,2),('C',4,1,2),('C',5,1,2),
('D',1,1,2),('D',2,1,2),('D',3,1,2),('D',4,1,2),('D',5,1,2);

-- Función 3 (Inception) — filas A-C, 6 asientos
INSERT INTO asiento (fila, numero, id_estado_asiento, id_funcion) VALUES
('A',1,1,3),('A',2,1,3),('A',3,1,3),('A',4,1,3),('A',5,1,3),('A',6,1,3),
('B',1,1,3),('B',2,2,3),('B',3,1,3),('B',4,1,3),('B',5,1,3),('B',6,1,3),
('C',1,1,3),('C',2,1,3),('C',3,1,3),('C',4,1,3),('C',5,1,3),('C',6,1,3);

-- Función 4 (La La Land) — filas A-B, 5 asientos
INSERT INTO asiento (fila, numero, id_estado_asiento, id_funcion) VALUES
('A',1,1,4),('A',2,1,4),('A',3,1,4),('A',4,1,4),('A',5,1,4),
('B',1,1,4),('B',2,1,4),('B',3,1,4),('B',4,1,4),('B',5,1,4);

-- Función 5 (It) — filas A-C, 5 asientos
INSERT INTO asiento (fila, numero, id_estado_asiento, id_funcion) VALUES
('A',1,1,5),('A',2,1,5),('A',3,1,5),('A',4,1,5),('A',5,1,5),
('B',1,2,5),('B',2,2,5),('B',3,1,5),('B',4,1,5),('B',5,1,5),
('C',1,1,5),('C',2,1,5),('C',3,1,5),('C',4,1,5),('C',5,1,5);

-- Función 6 (Rey León) — filas A-B, 6 asientos
INSERT INTO asiento (fila, numero, id_estado_asiento, id_funcion) VALUES
('A',1,1,6),('A',2,1,6),('A',3,1,6),('A',4,1,6),('A',5,1,6),('A',6,1,6),
('B',1,1,6),('B',2,1,6),('B',3,1,6),('B',4,1,6),('B',5,1,6),('B',6,1,6);

-- INVENTARIO (uno por cine)
INSERT INTO inventario_cine (stock_actual, stock_minimo, ultima_reposicion, id_cine) VALUES
    (500, 50, NOW(), 1),
    (300, 30, NOW(), 2);

-- CONFITERIA (ligada al inventario del cine correspondiente)
-- tipo = valor que el frontend manda como "Categoria": Snack / Bebida / Combo
INSERT INTO CONFITERIA (tipo, nombre, cantidad, precio_unitario, id_inventario) VALUES
    ('Snack',  'Popcorn Pequeño',   200, 12.00, 1),
    ('Snack',  'Popcorn Grande',    150, 20.00, 1),
    ('Bebida', 'Gaseosa Mediana',   300, 10.00, 1),
    ('Bebida', 'Gaseosa Grande',    250, 13.00, 1),
    ('Bebida', 'Agua Mineral',      400,  6.00, 1),
    ('Snack',  'Nachos con Queso',  100, 15.50, 1),
    ('Combo',  'Combo Pareja',       80, 40.00, 1),
    ('Combo',  'Combo Familiar',     40, 55.00, 1),
    ('Snack',  'Popcorn Pequeño',   120, 12.00, 2),
    ('Snack',  'Popcorn Grande',     90, 20.00, 2),
    ('Bebida', 'Gaseosa Grande',    150, 13.00, 2),
    ('Combo',  'Combo Pareja',       50, 40.00, 2);

-- CARTELERA (una por cine, enlazada con sus películas)
INSERT INTO CARTELERA (descripcion, id_cine) VALUES
    ('Cartelera Junio 2026 - Miraflores', 1),
    ('Cartelera Junio 2026 - San Isidro', 2);

INSERT INTO CARTELERA_PELICULA (id_cartelera, id_pelicula) VALUES
    (1,1),(1,2),(1,3),(1,4),(1,5),
    (2,1),(2,4),(2,5);

-- USUARIOS DE PRUEBA
-- IMPORTANTE: El BO usa BCrypt (at.favre.lib.crypto.bcrypt, cost 12).
-- Las contraseñas deben insertarse ya hasheadas — texto plano NO funciona.
--
-- Opción A (recomendada): Registra los usuarios desde el frontend (/register).
--   Cualquier email que termine en @cineflow.com tendrá rol de administrador.
--
-- Opción B: Genera el hash BCrypt cost-12 de tu contraseña en:
--   https://bcrypt.online  →  cost=12  →  copia el resultado y reemplaza abajo.
--
-- Ejemplo con contraseña "Admin123!" (reemplaza el hash por el tuyo):
-- INSERT INTO USUARIO (email, contrasenia, nombre, apellidos, telefono, fechaNacimiento, fechaRegistro) VALUES
--     ('admin@cineflow.com', '$2a$12$REEMPLAZA_CON_TU_HASH_AQUI', 'Admin', 'CineFlow', '999000001', '1990-01-01', NOW()),
--     ('cliente@gmail.com',  '$2a$12$REEMPLAZA_CON_TU_HASH_AQUI', 'María', 'García',   '999000003', '1995-03-20', NOW());


-- ============================================================
-- Restaurar configuración
-- ============================================================
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
