-- ============================================================
-- DATOS DEMO PARA LOS REPORTES (CineFlowReportes)
-- Crea reservas CONFIRMADAS completas (usuario, pago, asientos,
-- entradas, confiteria y comprobante) para que:
--   * /reportes/ventas-pelicula   muestre filas con recaudacion
--   * /reportes/comprobante?id=..  muestre un comprobante maestro-detalle
--
-- Ejecutar DESPUES de setup-completo.sql (necesita el catalogo ya cargado).
-- Idempotente-ish: usa un email/QR propios; si ya corriste antes, borra esos
-- registros o cambia los valores marcados con (*).
-- ============================================================

USE cineflow;

-- ---------- Usuario demo (password BCrypt = "Cliente123") ----------
INSERT INTO USUARIO (email, contrasenia, nombre, apellidos, telefono, fechaNacimiento, fechaRegistro)
VALUES ('reportes@gmail.com',                                        -- (*)
        '$2a$12$61WjMH2pByHUHoMq0y8MU.nVViaB5NfYvOzvsmceN4stA5ir/G.lC',
        'Diego', 'Demo', '999111222', '1998-07-15', NOW());
SET @idUser := LAST_INSERT_ID();

-- ============================================================
-- RESERVA 1  -> Funcion 1 (Avengers: Endgame, 2D, precio 18.00)
--   2 entradas + 1 popcorn grande + 2 gaseosas grandes
--   Total = 2*18 + 20 + 2*13 = 82.00
-- ============================================================
INSERT INTO PAGO (monto, fecha, estado, metodo)
VALUES (82.00, NOW(), 'APROBADO', 'TARJETA');
SET @idPago1 := LAST_INSERT_ID();

INSERT INTO RESERVA (fechaReserva, fechaExpiracion, estado, totalFinal, fid_usuario, fid_pago)
VALUES (NOW(), NULL, 'CONFIRMADA', 82.00, @idUser, @idPago1);
SET @idRes1 := LAST_INSERT_ID();

-- asientos (D1, D2 de la funcion 1) + marcarlos OCUPADO
INSERT INTO reserva_asiento (id_reserva, id_asiento)
SELECT @idRes1, id_asiento FROM asiento
WHERE id_funcion = 1 AND fila = 'D' AND numero IN (1,2);
UPDATE asiento SET id_estado_asiento = 2
WHERE id_funcion = 1 AND fila = 'D' AND numero IN (1,2);

-- entradas
INSERT INTO entrada (tipo, precio_base, id_reserva) VALUES
('REGULAR', 18.00, @idRes1),
('REGULAR', 18.00, @idRes1);

-- confiteria
INSERT INTO RESERVA_CONFITERIA (id_reserva, id_item, cantidad, precio_unitario)
SELECT @idRes1, id_item, 1, 20.00 FROM CONFITERIA WHERE nombre='Popcorn Grande' AND id_inventario=1 LIMIT 1;
INSERT INTO RESERVA_CONFITERIA (id_reserva, id_item, cantidad, precio_unitario)
SELECT @idRes1, id_item, 2, 13.00 FROM CONFITERIA WHERE nombre='Gaseosa Grande' AND id_inventario=1 LIMIT 1;

-- comprobante
INSERT INTO COMPROBANTE_COMPRA (codigo_qr, monto_total, id_reserva)
VALUES ('QR-DEMO-0001', 82.00, @idRes1);

-- ============================================================
-- RESERVA 2  -> Funcion 3 (Inception, 2D, precio 20.00)
--   3 entradas (2 regular + 1 nino) + 1 nachos
--   Total = 20 + 20 + 14 + 15.50 = 69.50
-- ============================================================
INSERT INTO PAGO (monto, fecha, estado, metodo)
VALUES (69.50, NOW(), 'APROBADO', 'BILLETERA_DIGITAL');
SET @idPago2 := LAST_INSERT_ID();

INSERT INTO RESERVA (fechaReserva, fechaExpiracion, estado, totalFinal, fid_usuario, fid_pago)
VALUES (NOW(), NULL, 'CONFIRMADA', 69.50, @idUser, @idPago2);
SET @idRes2 := LAST_INSERT_ID();

INSERT INTO reserva_asiento (id_reserva, id_asiento)
SELECT @idRes2, id_asiento FROM asiento
WHERE id_funcion = 3 AND fila = 'A' AND numero IN (1,2,3);
UPDATE asiento SET id_estado_asiento = 2
WHERE id_funcion = 3 AND fila = 'A' AND numero IN (1,2,3);

INSERT INTO entrada (tipo, precio_base, id_reserva) VALUES
('REGULAR', 20.00, @idRes2),
('REGULAR', 20.00, @idRes2),
('NINO',    14.00, @idRes2);

INSERT INTO RESERVA_CONFITERIA (id_reserva, id_item, cantidad, precio_unitario)
SELECT @idRes2, id_item, 1, 15.50 FROM CONFITERIA WHERE nombre='Nachos con Queso' AND id_inventario=1 LIMIT 1;

INSERT INTO COMPROBANTE_COMPRA (codigo_qr, monto_total, id_reserva)
VALUES ('QR-DEMO-0002', 69.50, @idRes2);

-- ============================================================
-- RESERVA 3  -> Funcion 5 (It: Capitulo 1, 3D, precio 22.00)  cine 2
--   2 entradas, sin confiteria.  Total = 44.00
--   (da una segunda pelicula/cine al reporte de ventas)
-- ============================================================
INSERT INTO PAGO (monto, fecha, estado, metodo)
VALUES (44.00, NOW(), 'APROBADO', 'TARJETA');
SET @idPago3 := LAST_INSERT_ID();

INSERT INTO RESERVA (fechaReserva, fechaExpiracion, estado, totalFinal, fid_usuario, fid_pago)
VALUES (NOW(), NULL, 'CONFIRMADA', 44.00, @idUser, @idPago3);
SET @idRes3 := LAST_INSERT_ID();

INSERT INTO reserva_asiento (id_reserva, id_asiento)
SELECT @idRes3, id_asiento FROM asiento
WHERE id_funcion = 5 AND fila = 'C' AND numero IN (1,2);
UPDATE asiento SET id_estado_asiento = 2
WHERE id_funcion = 5 AND fila = 'C' AND numero IN (1,2);

INSERT INTO entrada (tipo, precio_base, id_reserva) VALUES
('REGULAR', 22.00, @idRes3),
('REGULAR', 22.00, @idRes3);

INSERT INTO COMPROBANTE_COMPRA (codigo_qr, monto_total, id_reserva)
VALUES ('QR-DEMO-0003', 44.00, @idRes3);

-- ============================================================
-- Comprueba que quedaron creadas (los ids para probar el comprobante):
SELECT @idRes1 AS reserva1, @idRes2 AS reserva2, @idRes3 AS reserva3;
-- Reporte ventas:    http://localhost:8080/CineFlowReportes/reportes/ventas-pelicula
-- Reporte comprobante: .../reportes/comprobante?id=  (usa uno de los ids de arriba)
-- ============================================================
