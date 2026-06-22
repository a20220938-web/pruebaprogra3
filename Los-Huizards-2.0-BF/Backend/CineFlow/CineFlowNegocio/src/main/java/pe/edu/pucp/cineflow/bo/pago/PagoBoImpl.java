package pe.edu.pucp.cineflow.bo.pago;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.TransactionsManager;
import pe.edu.pucp.cineflow.dao.pago.IPagoDao;
import pe.edu.pucp.cineflow.dao.pago.PagoDaoImpl;
import pe.edu.pucp.cineflow.dao.reporte.ComprobanteCompraDaoImpl;
import pe.edu.pucp.cineflow.dao.reporte.IComprobanteCompraDao;
import pe.edu.pucp.cineflow.dao.reserva.AsientoDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.IAsientoDao;
import pe.edu.pucp.cineflow.dao.reserva.IInventarioCineDao;
import pe.edu.pucp.cineflow.dao.reserva.IReservaDao;
import pe.edu.pucp.cineflow.dao.reserva.InventarioCineDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.ReservaDaoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.pago.EstadoPago;
import pe.edu.pucp.cineflow.modelo.pago.MetodoPago;
import pe.edu.pucp.cineflow.modelo.pago.Pago;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.Confiteria;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoReserva;
import pe.edu.pucp.cineflow.modelo.reserva.InventarioCine;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PagoBoImpl extends BaseBo implements IPagoBo {

    private final IPagoDao            pagoDao       = new PagoDaoImpl();
    private final IReservaDao         reservaDao    = new ReservaDaoImpl();
    private final IAsientoDao         asientoDao    = new AsientoDaoImpl();
    private final IInventarioCineDao  inventarioDao = new InventarioCineDaoImpl();
    private final IComprobanteCompraDao comprobanteDao = new ComprobanteCompraDaoImpl();

    @Override
    public List<Pago> listar() {
        return pagoDao.leerTodos();
    }

    @Override
    public Pago obtener(int id) {
        validarIdPositivo(id, "id de pago");
        return pagoDao.leer(id);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id de pago");
        if (!pagoDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar el pago con id: " + id);
    }

    @Override
    public void guardar(Pago modelo, Estado estado) {
        validarPago(modelo);
        validarEstado(estado);

        if (estado == Estado.Nuevo) {
            int id = pagoDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo registrar el pago");
            modelo.setIdPago(id);
        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getIdPago(), "id de pago");
            if (!pagoDao.actualizar(modelo))
                throw new IllegalStateException("No se pudo actualizar el pago con id: " + modelo.getIdPago());
        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }

    @Override
    public void aprobarPago(int idPago) {
        validarIdPositivo(idPago, "id de pago");
        Pago pago = pagoDao.leer(idPago);
        if (pago == null)
            throw new IllegalStateException("No existe el pago con id: " + idPago);

        if (pago.getEstado() == EstadoPago.APROBADO)
            throw new IllegalStateException("El pago ya está aprobado");

        pago.setEstado(EstadoPago.APROBADO);
        if (!pagoDao.actualizar(pago))
            throw new IllegalStateException("No se pudo aprobar el pago");
    }

    @Override
    public void rechazarPago(int idPago) {
        validarIdPositivo(idPago, "id de pago");
        Pago pago = pagoDao.leer(idPago);
        if (pago == null)
            throw new IllegalStateException("No existe el pago con id: " + idPago);

        if (pago.getEstado() == EstadoPago.FALLIDO)
            throw new IllegalStateException("El pago ya está rechazado");

        pago.setEstado(EstadoPago.FALLIDO);
        if (!pagoDao.actualizar(pago))
            throw new IllegalStateException("No se pudo rechazar el pago");
    }



    // RF13 - Métodos de pago disponibles
    @Override
    public List<String> listarMetodosPago() {
        return Arrays.stream(MetodoPago.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // RF14 - Confirmar transacción: reserva CONFIRMADA, asientos OCUPADO, stock↓, QR generado
    @Override
    public ComprobanteCompra confirmarTransaccion(int idPago) {
        validarIdPositivo(idPago, "id de pago");

        Pago pago = pagoDao.leer(idPago);
        if (pago == null)
            throw new IllegalStateException("No existe el pago con id: " + idPago);
        if (pago.getEstado() != EstadoPago.APROBADO)
            throw new IllegalStateException("El pago no está aprobado; estado actual: " + pago.getEstado());

        // Buscar la reserva asociada al pago
        Reserva reserva = reservaDao.leerTodos().stream()
                .filter(r -> r.getPago() != null && r.getPago().getIdPago() == idPago)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró reserva asociada al pago: " + idPago));

        TransactionsManager.iniciarTransaccion();
        try {
            // 1. Confirmar reserva
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reservaDao.actualizar(reserva);

            // 2. Asientos → OCUPADO (RF14)
            List<Asiento> asientos = reserva.getAsientos();
            if (asientos != null) {
                for (Asiento asiento : asientos) {
                    asiento.setEstado(EstadoAsiento.OCUPADO);
                    asientoDao.actualizar(asiento);
                }
            }

            // 3. Descontar stock de confitería (RF14)
            Confiteria confiteria = reserva.getConfiteria();
            if (confiteria != null && confiteria.getCantidad() > 0 && reserva.getInventario() != null) {
                InventarioCine inventario = inventarioDao.leer(reserva.getInventario().getIdInventario());
                if (inventario != null) {
                    int nuevoStock = inventario.getStockActual() - confiteria.getCantidad();
                    if (nuevoStock < 0)
                        throw new IllegalStateException("Stock insuficiente para completar la transacción");
                    inventario.setStockActual(nuevoStock);
                    inventarioDao.actualizar(inventario);
                }
            }

            // 4. Generar comprobante con código QR (RF14/RF16)
            ComprobanteCompra comprobante = comprobanteDao.leerPorReserva(reserva.getIdReserva());
            if (comprobante == null) {
                comprobante = new ComprobanteCompra();
                comprobante.setReserva(reserva);
                comprobante.setMontoTotal(reserva.getTotalFinal());
                comprobante.setCodigoQR("QR-" + reserva.getIdReserva() + "-" + System.currentTimeMillis());
                int idComp = comprobanteDao.crear(comprobante);
                comprobante.setIdComprobante(idComp);
            }

            TransactionsManager.commitTransaccion();
            return comprobante;
        } catch (Exception e) {
            TransactionsManager.rollbackTransaccion();
            throw e;
        }
    }

    // RF15 - Revertir transacción fallida: libera asientos, repone stock, reserva→CANCELADA
    @Override
    public void revertirTransaccion(int idPago) {
        validarIdPositivo(idPago, "id de pago");

        Pago pago = pagoDao.leer(idPago);
        if (pago == null)
            throw new IllegalStateException("No existe el pago con id: " + idPago);
        if (pago.getEstado() != EstadoPago.FALLIDO)
            throw new IllegalStateException("Solo se puede revertir un pago en estado FALLIDO");

        Reserva reserva = reservaDao.leerTodos().stream()
                .filter(r -> r.getPago() != null && r.getPago().getIdPago() == idPago)
                .findFirst()
                .orElse(null);

        if (reserva == null) return;

        TransactionsManager.iniciarTransaccion();
        try {
            // 1. Cancelar la reserva
            reserva.setEstado(EstadoReserva.CANCELADA);
            reservaDao.actualizar(reserva);

            // 2. Liberar asientos → DISPONIBLE (RF15)
            List<Asiento> asientos = reserva.getAsientos();
            if (asientos != null) {
                for (Asiento asiento : asientos) {
                    asiento.setEstado(EstadoAsiento.DISPONIBLE);
                    asientoDao.actualizar(asiento);
                }
            }

            // 3. Reponer stock de confitería (RF15)
            Confiteria confiteria = reserva.getConfiteria();
            if (confiteria != null && confiteria.getCantidad() > 0 && reserva.getInventario() != null) {
                InventarioCine inventario = inventarioDao.leer(reserva.getInventario().getIdInventario());
                if (inventario != null) {
                    inventario.setStockActual(inventario.getStockActual() + confiteria.getCantidad());
                    inventarioDao.actualizar(inventario);
                }
            }

            TransactionsManager.commitTransaccion();
        } catch (Exception e) {
            TransactionsManager.rollbackTransaccion();
            throw e;
        }
    }

    private void validarPago(Pago modelo) {
        if (modelo == null)
            throw new IllegalArgumentException("El pago no puede ser nulo");
        validarMontoPositivo(modelo.getMonto(), "monto del pago");
        if (modelo.getMonto() == 0)
            throw new IllegalArgumentException("El monto del pago debe ser mayor a 0");
        if (modelo.getFecha() == null)
            throw new IllegalArgumentException("La fecha del pago es obligatoria");
    }
}