package pe.edu.pucp.cineflow.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import pe.edu.pucp.cineflow.bo.reserva.IReservaBo;
import pe.edu.pucp.cineflow.bo.reserva.ReservaBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;

import java.util.List;

@WebService(
        serviceName = "ReservasWS",
        targetNamespace = "http://services.cineflow.pucp.edu.pe/")
public class ReservasWS {

    private final IReservaBo reservaBo;

    public ReservasWS() {
        this.reservaBo = new ReservaBoImpl();
    }

    @WebMethod(operationName = "listarReservas")
    public List<Reserva> listarReservas() {
        return reservaBo.listar();
    }

    @WebMethod(operationName = "obtenerReserva")
    public Reserva obtenerReserva(@WebParam(name = "id") int id) {
        return reservaBo.obtener(id);
    }

    @WebMethod(operationName = "eliminarReserva")
    public void eliminarReserva(@WebParam(name = "id") int id) {
        reservaBo.eliminar(id);
    }

    @WebMethod(operationName = "guardarReserva")
    public void guardarReserva(@WebParam(name = "reserva") Reserva reserva,
                               @WebParam(name = "estado") Estado estado) {
        reservaBo.guardar(reserva, estado);
    }

    @WebMethod(operationName = "listarReservasPorUsuario")
    public List<Reserva> listarReservasPorUsuario(@WebParam(name = "idUsuario") int idUsuario) {
        return reservaBo.listarPorUsuario(idUsuario);
    }

    @WebMethod(operationName = "listarReservasPorFuncion")
    public List<Reserva> listarReservasPorFuncion(@WebParam(name = "idFuncion") int idFuncion) {
        return reservaBo.listarPorFuncion(idFuncion);
    }

    @WebMethod(operationName = "confirmarReserva")
    public void confirmarReserva(@WebParam(name = "idReserva") int idReserva) {
        reservaBo.confirmarReserva(idReserva);
    }

    @WebMethod(operationName = "cancelarReserva")
    public void cancelarReserva(@WebParam(name = "idReserva") int idReserva) {
        reservaBo.cancelarReserva(idReserva);
    }

    @WebMethod(operationName = "generarComprobante")
    public ComprobanteCompra generarComprobante(@WebParam(name = "idReserva") int idReserva) {
        return reservaBo.generarComprobante(idReserva);
    }
}
