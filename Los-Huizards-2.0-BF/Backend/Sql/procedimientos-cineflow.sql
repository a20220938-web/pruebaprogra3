-- ============================================================
-- CineFlow — Stored Procedures para MySQL
-- Sigue el mismo patrón que SoftProg:
--   IN p_campo TIPO  →  parámetros de entrada
--   OUT p_id INT     →  id generado en INSERT
-- ============================================================

USE cineflow;

-- ============================================================
-- USUARIO
-- ============================================================
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
    INSERT INTO USUARIO (email, contrasenia, nombre, apellidos,
                         telefono, fechaNacimiento, fechaRegistro)
    VALUES (p_email, p_contrasenia, p_nombre, p_apellidos,
            p_telefono, p_fechaNacimiento, p_fechaRegistro);
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
    SET email           = p_email,
        contrasenia     = p_contrasenia,
        nombre          = p_nombre,
        apellidos       = p_apellidos,
        telefono        = p_telefono,
        fechaNacimiento = p_fechaNacimiento
    WHERE idUsuario = p_id;
END //

CREATE PROCEDURE eliminarUsuario(IN p_id INT)
BEGIN
    DELETE FROM USUARIO WHERE idUsuario = p_id;
END //

CREATE PROCEDURE buscarUsuarioPorId(IN p_id INT)
BEGIN
    SELECT * FROM USUARIO WHERE idUsuario = p_id;
END //

CREATE PROCEDURE listarUsuarios()
BEGIN
    SELECT * FROM USUARIO;
END //

CREATE PROCEDURE buscarUsuarioPorEmail(IN p_email VARCHAR(120))
BEGIN
    SELECT * FROM USUARIO WHERE email = p_email;
END //

DELIMITER ;


-- ============================================================
-- PELICULA
-- ============================================================
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
    SET titulo           = p_titulo,
        duracion         = p_duracion,
        sinopsis         = p_sinopsis,
        edad_restriccion = p_edad_restriccion,
        genero           = p_genero
    WHERE id = p_id;
END //

CREATE PROCEDURE eliminarPelicula(IN p_id INT)
BEGIN
    DELETE FROM PELICULA WHERE id = p_id;
END //

CREATE PROCEDURE buscarPeliculaPorId(IN p_id INT)
BEGIN
    SELECT * FROM PELICULA WHERE id = p_id;
END //

CREATE PROCEDURE listarPeliculas()
BEGIN
    SELECT * FROM PELICULA;
END //

DELIMITER ;


-- ============================================================
-- PAGO
-- ============================================================
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
    INSERT INTO PAGO (monto, fecha, estado, metodo)
    VALUES (p_monto, p_fecha, p_estado, p_metodo);
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE modificarPago(
    IN p_estado VARCHAR(30),
    IN p_monto  DECIMAL(10,2),
    IN p_metodo VARCHAR(40),
    IN p_id     INT)
BEGIN
    UPDATE PAGO
    SET estado = p_estado,
        monto  = p_monto,
        metodo = p_metodo
    WHERE idPago = p_id;
END //

CREATE PROCEDURE eliminarPago(IN p_id INT)
BEGIN
    DELETE FROM PAGO WHERE idPago = p_id;
END //

CREATE PROCEDURE buscarPagoPorId(IN p_id INT)
BEGIN
    SELECT * FROM PAGO WHERE idPago = p_id;
END //

CREATE PROCEDURE listarPagos()
BEGIN
    SELECT * FROM PAGO;
END //

DELIMITER ;


-- ============================================================
-- RESERVA
-- ============================================================
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
    INSERT INTO RESERVA (fechaReserva, fechaExpiracion, estado,
                         totalFinal, fid_usuario, fid_pago)
    VALUES (p_fechaReserva, p_fechaExpiracion, p_estado,
            p_totalFinal, p_fid_usuario, p_fid_pago);
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
    SET fechaReserva    = p_fechaReserva,
        fechaExpiracion = p_fechaExpiracion,
        estado          = p_estado,
        totalFinal      = p_totalFinal,
        fid_usuario     = p_fid_usuario,
        fid_pago        = p_fid_pago
    WHERE idReserva = p_id;
END //

CREATE PROCEDURE eliminarReserva(IN p_id INT)
BEGIN
    DELETE FROM RESERVA WHERE idReserva = p_id;
END //

CREATE PROCEDURE buscarReservaPorId(IN p_id INT)
BEGIN
    SELECT * FROM RESERVA WHERE idReserva = p_id;
END //

CREATE PROCEDURE listarReservas()
BEGIN
    SELECT * FROM RESERVA;
END //

DELIMITER ;


-- ============================================================
-- COMPROBANTE_COMPRA
-- ============================================================
DROP PROCEDURE IF EXISTS insertarComprobante;
DROP PROCEDURE IF EXISTS eliminarComprobante;
DROP PROCEDURE IF EXISTS buscarComprobantePorId;
DROP PROCEDURE IF EXISTS buscarComprobantePorReserva;
DROP PROCEDURE IF EXISTS listarComprobantes;

DELIMITER //

CREATE PROCEDURE insertarComprobante(
    IN  p_codigo_qr  VARCHAR(255),
    IN  p_monto_total DECIMAL(10,2),
    IN  p_id_reserva  INT,
    OUT p_id          INT)
BEGIN
    INSERT INTO COMPROBANTE_COMPRA (codigo_qr, monto_total, id_reserva)
    VALUES (p_codigo_qr, p_monto_total, p_id_reserva);
    SET p_id = LAST_INSERT_ID();
END //

CREATE PROCEDURE eliminarComprobante(IN p_id INT)
BEGIN
    DELETE FROM COMPROBANTE_COMPRA WHERE id_comprobante = p_id;
END //

CREATE PROCEDURE buscarComprobantePorId(IN p_id INT)
BEGIN
    SELECT * FROM COMPROBANTE_COMPRA WHERE id_comprobante = p_id;
END //

CREATE PROCEDURE buscarComprobantePorReserva(IN p_id_reserva INT)
BEGIN
    SELECT * FROM COMPROBANTE_COMPRA WHERE id_reserva = p_id_reserva LIMIT 1;
END //

CREATE PROCEDURE listarComprobantes()
BEGIN
    SELECT * FROM COMPROBANTE_COMPRA;
END //

DELIMITER ;
