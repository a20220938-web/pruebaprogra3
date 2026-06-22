package pe.edu.pucp.cineflow.bo.pago;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.pago.Pago;

public interface IPagoBo extends IGestionable<Pago> {
    void aprobarPago(int idPago);
    void rechazarPago(int idPago);
    // RF13 - Listar métodos de pago disponibles
    java.util.List<String> listarMetodosPago();
    // RF14 - Confirmar pago: actualiza reserva, asientos, stock y genera comprobante
    pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra confirmarTransaccion(int idPago);
    // RF15 - Revertir pago fallido: libera asientos y repone stock
    void revertirTransaccion(int idPago);
}