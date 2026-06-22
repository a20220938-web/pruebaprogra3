package pe.edu.pucp.cineflow.bo.reserva;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;

import java.util.List;

public interface IReservaBo extends IGestionable<Reserva> {
    List<Reserva> listarPorUsuario(int idUsuario);
    List<Reserva> listarPorFuncion(int idFuncion);
    void confirmarReserva(int idReserva);
    void cancelarReserva(int idReserva);
    ComprobanteCompra generarComprobante(int idReserva);
    long obtenerTiempoRestanteSegundos(int idReserva);
    void expirarSiCorresponde(int idReserva);
}