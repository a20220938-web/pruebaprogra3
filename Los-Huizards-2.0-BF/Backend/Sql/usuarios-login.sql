-- ============================================================
-- Usuarios de login para la demo (Lab 13)
-- Contrasenas en BCrypt cost 12 -> compatibles con UsuarioBoImpl
-- (at.favre BCrypt.verifyer().verify acepta $2a$).
-- Ejecutar DESPUES de setup-completo.sql.
--
-- Credenciales:
--   cliente@gmail.com   / Cliente123   (usuario normal)
--   admin@cineflow.com  / Admin123     (ADMIN: el dominio @cineflow.com lo hace admin)
--
-- REGLA DE NEGOCIO: es admin todo correo que termine en @cineflow.com
-- (ver UserState.IsAdmin). Por eso los clientes NO deben usar @cineflow.com.
--
-- NOTA: si el INSERT falla por email duplicado, primero borra esos correos
--       o registralos desde la app y documenta esas credenciales.
-- ============================================================

USE cineflow;

INSERT INTO USUARIO (email, contrasenia, nombre, apellidos, telefono, fechaNacimiento, fechaRegistro)
VALUES
('cliente@gmail.com', '$2a$12$61WjMH2pByHUHoMq0y8MU.nVViaB5NfYvOzvsmceN4stA5ir/G.lC',
 'Maria', 'Garcia', '999000001', '1995-03-20', NOW()),
('admin@cineflow.com',   '$2a$12$CZq3Xq8v1OZaX/x/eva8u.5AkxSF5I527dLndQ3jhZma6nCG9Y1FS',
 'Eric', 'Huiza',  '999000002', '1990-01-10', NOW());

-- Verificacion rapida:
-- SELECT idUsuario, email, nombre, apellidos FROM USUARIO WHERE email IN
--   ('cliente@gmail.com','admin@cineflow.com');
