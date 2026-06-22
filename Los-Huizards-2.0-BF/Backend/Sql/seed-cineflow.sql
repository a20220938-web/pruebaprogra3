-- ============================================================
-- CineFlow — Datos semilla para tablas catálogo
-- Ejecutar UNA SOLA VEZ, después de script_1.sql
-- Los valores deben coincidir exactamente con los enums Java
-- ============================================================

USE cineflow;

-- ============================================================
-- ESTADO_ASIENTO
-- Enum Java: EstadoAsiento { DISPONIBLE, OCUPADO, RESERVADO }
-- ============================================================
INSERT INTO ESTADO_ASIENTO (nombre) VALUES
    ('DISPONIBLE'),
    ('OCUPADO'),
    ('RESERVADO');

-- ============================================================
-- FORMATO_PROYECCION
-- Enum Java: FormatoProyeccion { DosD, tresD, IMAX }
-- ============================================================
INSERT INTO FORMATO_PROYECCION (nombre) VALUES
    ('DosD'),
    ('tresD'),
    ('IMAX');

-- ============================================================
-- ESTADO_PAGO
-- Enum Java: EstadoPago { PENDIENTE, PROCESANDO, APROBADO, FALLIDO }
-- ============================================================
INSERT INTO ESTADO_PAGO (nombre) VALUES
    ('PENDIENTE'),
    ('PROCESANDO'),
    ('APROBADO'),
    ('FALLIDO');

-- ============================================================
-- ESTADO_RESERVA
-- Enum Java: EstadoReserva { PENDIENTE, CONFIRMADA, CANCELADA }
-- ============================================================
INSERT INTO estado_reserva (nombre) VALUES
    ('PENDIENTE'),
    ('CONFIRMADA'),
    ('CANCELADA');

-- ============================================================
-- METODO_PAGO
-- Enum Java: MetodoPago { TARJETA, BILLETERA_DIGITAL }
-- ============================================================
INSERT INTO METODO_PAGO (nombre) VALUES
    ('TARJETA'),
    ('BILLETERA_DIGITAL');
