package pe.edu.pucp.cineflow.bo.reserva;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.TransactionsManager;
import pe.edu.pucp.cineflow.dao.reporte.ComprobanteCompraDaoImpl;
import pe.edu.pucp.cineflow.dao.reporte.IComprobanteCompraDao;
import pe.edu.pucp.cineflow.dao.reserva.AsientoDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.IAsientoDao;
import pe.edu.pucp.cineflow.dao.reserva.IInventarioCineDao;
import pe.edu.pucp.cineflow.dao.reserva.IReservaDao;
import pe.edu.pucp.cineflow.dao.reserva.InventarioCineDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.ReservaDaoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.Confiteria;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoReserva;
import pe.edu.pucp.cineflow.modelo.reserva.InventarioCine;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservaBoImpl extends BaseBo implements IReservaBo {

    private final IReservaDao          reservaDao     = new ReservaDaoImpl();
    private final IComprobanteCompraDao comprobanteDao = new ComprobanteCompraDaoImpl();
    private final IAsientoDao          asientoDao     = new AsientoDaoImpl();
    private final IInventarioCineDao   inventarioDao  = new InventarioCineDaoImpl();

    @Override
    public List<Reserva> listar() {
        return reservaDao.leerTodos();
    }

    @Override
    public List<Reserva> listarPorUsuario(int idUsuario) {
        validarIdPositivo(idUsuario, "id de usuario");
        return reservaDao.leerPorUsuario(idUsuario);
    }

    @Override
    public List<Reserva> listarPorFuncion(int idFuncion) {
        validarIdPositivo(idFuncion, "id de función");
        return reservaDao.leerPorFuncion(idFuncion);
    }

    @Override
    public Reserva obtener(int id) {
        validarIdPositivo(id, "id de reserva");
        return reservaDao.leer(id);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id de reserva");
        TransactionsManager.iniciarTransaccion();
        try {
            if (!reservaDao.eliminar(id))
                throw new IllegalStateException("No se pudo eliminar la reserva con id: " + id);
            TransactionsManager.commitTransaccion();
        } catch (Exception e) {
            TransactionsManager.rollbackTransaccion();
            throw e;
        }
    }


    @Override
    public void guardar(Reserva modelo, Estado estado) {
        validarReserva(modelo);
        validarEstado(estado);

        TransactionsManager.iniciarTransaccion();
        try {
            if (estado == Estado.Nuevo) {
                modelo.setFechaReserva(LocalDateTime.now());
                modelo.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
                modelo.setEstado(EstadoReserva.PENDIENTE);
                modelo.setTotalFinal(calcularTotal(modelo));
                int id = reservaDao.crear(modelo);
                if (id <= 0)
                    throw new IllegalStateException("No se pudo crear la reserva");
                modelo.setIdReserva(id);

                // Marcar asientos como RESERVADO al crear la reserva.
                // Si ya están RESERVADO (pre-reservados al avanzar desde selección), se aceptan.
                List<Asiento> asientos = modelo.getAsientos();
                if (asientos != null) {
                    for (Asiento asiento : asientos) {
                        Asiento dbAsiento = asientoDao.leer(asiento.getIdAsiento());
                        if (dbAsiento == null)
                            throw new IllegalStateException("No existe el asiento con id: " + asiento.getIdAsiento());
                        if (dbAsiento.getEstado() == EstadoAsiento.OCUPADO)
                            throw new IllegalStateException("El asiento " + asiento.getIdAsiento() + " ya está ocupado");
                        if (dbAsiento.getEstado() == EstadoAsiento.DISPONIBLE) {
                            dbAsiento.setEstado(EstadoAsiento.RESERVADO);
                            if (!asientoDao.actualizar(dbAsiento))
                                throw new IllegalStateException("No se pudo reservar el asiento id: " + asiento.getIdAsiento());
                        }
                        // Si ya está RESERVADO, no se modifica (pre-reservado previamente)
                    }
                }
            } else if (estado == Estado.Modificado) {
                validarIdPositivo(modelo.getIdReserva(), "id de reserva");
                if (!reservaDao.actualizar(modelo))
                    throw new IllegalStateException(
                            "No se pudo actualizar la reserva con id: " + modelo.getIdReserva());
            } else {
                throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
            }
            TransactionsManager.commitTransaccion();
        } catch (Exception e) {
            TransactionsManager.rollbackTransaccion();
            throw e;
        }
    }

    @Override
    public void confirmarReserva(int idReserva) {
        validarIdPositivo(idReserva, "id de reserva");

        Reserva reserva = reservaDao.leer(idReserva);
        if (reserva == null)
            throw new IllegalStateException("No existe la reserva con id: " + idReserva);

        if (reserva.getEstado() == EstadoReserva.CONFIRMADA)
            throw new IllegalStateException("La reserva ya está confirmada");

        if (reserva.getEstado() == EstadoReserva.CANCELADA)
            throw new IllegalStateException("No se puede confirmar una reserva cancelada");

        if (reserva.getFechaExpiracion() != null &&
                reserva.getFechaExpiracion().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("La reserva ha expirado y no puede confirmarse");

        TransactionsManager.iniciarTransaccion();
        try {
            // 1. Cambiar estado de la reserva a CONFIRMADA
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            if (!reservaDao.actualizar(reserva))
                throw new IllegalStateException("No se pudo confirmar la reserva");

            // 2. Cambiar estado de cada asiento a OCUPADO
            List<Asiento> asientos = reserva.getAsientos();
            if (asientos != null) {
                for (Asiento asiento : asientos) {
                    asiento.setEstado(EstadoAsiento.OCUPADO);
                    if (!asientoDao.actualizar(asiento))
                        throw new IllegalStateException(
                                "No se pudo actualizar el estado del asiento id: "
                                        + asiento.getIdAsiento());
                }
            }

            TransactionsManager.commitTransaccion();
        } catch (Exception e) {
            TransactionsManager.rollbackTransaccion();
            throw e;
        }
    }

    @Override
    public void cancelarReserva(int idReserva) {
        validarIdPositivo(idReserva, "id de reserva");

        Reserva reserva = reservaDao.leer(idReserva);
        if (reserva == null)
            throw new IllegalStateException("No existe la reserva con id: " + idReserva);

        // RF11: solo se puede cancelar si está PENDIENTE o CONFIRMADA
        if (reserva.getEstado() == EstadoReserva.CANCELADA)
            throw new IllegalStateException("La reserva ya está cancelada");

        boolean estabaConfirmada = reserva.getEstado() == EstadoReserva.CONFIRMADA;
        TransactionsManager.iniciarTransaccion();
        try {
            // 1. Cambiar estado de la reserva a CANCELADA
            reserva.setEstado(EstadoReserva.CANCELADA);
            if (!reservaDao.actualizar(reserva))
                throw new IllegalStateException("No se pudo cancelar la reserva");

            // 2. Liberar los asientos de vuelta a DISPONIBLE (RF11)
            List<Asiento> asientos = reserva.getAsientos();
            if (asientos != null) {
                for (Asiento asiento : asientos) {
                    asiento.setEstado(EstadoAsiento.DISPONIBLE);
                    if (!asientoDao.actualizar(asiento))
                        throw new IllegalStateException(
                                "No se pudo liberar el asiento id: " + asiento.getIdAsiento());
                }
            }

            // 3. Reponer stock de confitería SOLO si la reserva ya estaba confirmada
            //    (si estaba PENDIENTE, el stock nunca se descontó)
            if (estabaConfirmada) {
                reservaDao.reponerStockConfiteriaPorReserva(idReserva);
            }

            TransactionsManager.commitTransaccion();
        } catch (Exception e) {
            TransactionsManager.rollbackTransaccion();
            throw e;
        }
    }

    @Override
    public void expirarReservasVencidasPorFuncion(int idFuncion) {
        List<Reserva> reservas = reservaDao.leerPorFuncion(idFuncion);
        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.PENDIENTE
                    && r.getFechaExpiracion() != null
                    && LocalDateTime.now().isAfter(r.getFechaExpiracion())) {
                cancelarReserva(r.getIdReserva());
            }
        }
    }

    // RF12 - Tiempo restante del temporizador de reserva (en segundos)
    @Override
    public long obtenerTiempoRestanteSegundos(int idReserva) {
        validarIdPositivo(idReserva, "id de reserva");
        Reserva reserva = reservaDao.leer(idReserva);
        if (reserva == null)
            throw new IllegalStateException("No existe la reserva con id: " + idReserva);
        if (reserva.getFechaExpiracion() == null) return 0;
        long segundos = ChronoUnit.SECONDS.between(LocalDateTime.now(), reserva.getFechaExpiracion());
        return Math.max(0, segundos);
    }

    // RF12 - Verifica si la reserva expiró y la cancela automáticamente
    @Override
    public void expirarSiCorresponde(int idReserva) {
        validarIdPositivo(idReserva, "id de reserva");
        Reserva reserva = reservaDao.leer(idReserva);
        if (reserva == null)
            throw new IllegalStateException("No existe la reserva con id: " + idReserva);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) return;
        if (reserva.getFechaExpiracion() == null) return;
        if (LocalDateTime.now().isAfter(reserva.getFechaExpiracion())) {
            cancelarReserva(idReserva);
        }
    }


    @Override
    public ComprobanteCompra generarComprobante(int idReserva) {
        validarIdPositivo(idReserva, "id de reserva");

        Reserva reserva = reservaDao.leer(idReserva);
        if (reserva == null)
            throw new IllegalStateException("No existe la reserva con id: " + idReserva);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA)
            throw new IllegalStateException(
                    "Solo se puede generar comprobante de reservas confirmadas");

        ComprobanteCompra existente = comprobanteDao.leerPorReserva(idReserva);
        if (existente != null) return existente;

        ComprobanteCompra comprobante = new ComprobanteCompra();
        comprobante.setReserva(reserva);
        comprobante.setMontoTotal(reserva.getTotalFinal());
        comprobante.setCodigoQR("QR-" + idReserva + "-" + System.currentTimeMillis());

        TransactionsManager.iniciarTransaccion();
        try {
            int id = comprobanteDao.crear(comprobante);
            if (id <= 0)
                throw new IllegalStateException("No se pudo generar el comprobante");
            comprobante.setIdComprobante(id);
            TransactionsManager.commitTransaccion();
        } catch (Exception e) {
            TransactionsManager.rollbackTransaccion();
            throw e;
        }

        return comprobante;
    }


    private void validarReserva(Reserva modelo) {
        if (modelo == null)
            throw new IllegalArgumentException("La reserva no puede ser nula");
        if (modelo.getUsuario() == null)
            throw new IllegalArgumentException("El usuario es obligatorio");
        validarIdPositivo(modelo.getUsuario().getIdUsuario(), "id de usuario");

        if (modelo.getEntradas() == null || modelo.getEntradas().isEmpty())
            throw new IllegalArgumentException("La reserva debe tener al menos una entrada");
    }

    private double calcularTotal(Reserva modelo) {
        double totalEntradas = 0;
        if (modelo.getEntradas() != null)
            totalEntradas = modelo.getEntradas().stream()
                    .mapToDouble(e -> e.calcularPrecio())
                    .sum();

        double totalConfiteria = 0;
        if (modelo.getConfiteria() != null)
            totalConfiteria = modelo.getConfiteria().getPrecioUnitario()
                    * modelo.getConfiteria().getCantidad();

        // Sumar también la lista de snacks (confiterias) seleccionados en la reserva
        if (modelo.getConfiterias() != null)
            for (pe.edu.pucp.cineflow.modelo.reserva.ArticuloIndividual c : modelo.getConfiterias())
                totalConfiteria += c.getPrecioUnitario() * c.getCantidad();

        return totalEntradas + totalConfiteria;
    }
}