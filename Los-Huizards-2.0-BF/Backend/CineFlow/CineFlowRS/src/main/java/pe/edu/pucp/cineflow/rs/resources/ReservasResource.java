package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pe.edu.pucp.cineflow.bo.reserva.IReservaBo;
import pe.edu.pucp.cineflow.bo.reserva.ReservaBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.pago.EstadoPago;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;
import pe.edu.pucp.cineflow.modelo.reporte.ResumenCompra;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;

import java.time.LocalDateTime;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/v1/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservasResource {

    private final IReservaBo reservaBo;

    @Context
    private UriInfo uriInfo;

    public ReservasResource() {
        this.reservaBo = new ReservaBoImpl();
    }

    @GET
    public List<Reserva> listar() {
        return reservaBo.listar();
    }

    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        Reserva reserva = reservaBo.obtener(id);
        if (reserva == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Reserva con id " + id + " no encontrada"))
                    .build();
        }
        return Response.ok(reserva).build();
    }

    @GET
    @Path("usuario/{idUsuario}")
    public Response listarPorUsuario(@PathParam("idUsuario") int idUsuario) {
        List<Reserva> reservas = reservaBo.listarPorUsuario(idUsuario);
        if (reservas == null || reservas.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No hay reservas para el usuario con id " + idUsuario))
                    .build();
        }
        return Response.ok(reservas).build();
    }

    @GET
    @Path("funcion/{idFuncion}")
    public Response listarPorFuncion(@PathParam("idFuncion") int idFuncion) {
        List<Reserva> reservas = reservaBo.listarPorFuncion(idFuncion);
        if (reservas == null || reservas.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No hay reservas para la funcion con id " + idFuncion))
                    .build();
        }
        return Response.ok(reservas).build();
    }

    @POST
    public Response crear(Reserva reserva) {
        if (reserva == null || reserva.getUsuario() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload de reserva es invalido"))
                    .build();
        }
        // Auto-completar fecha y estado del pago si viene embebido sin esos campos
        if (reserva.getPago() != null) {
            if (reserva.getPago().getFecha() == null)
                reserva.getPago().setFecha(LocalDateTime.now());
            if (reserva.getPago().getEstado() == null)
                reserva.getPago().setEstado(EstadoPago.PENDIENTE);
        }
        reservaBo.guardar(reserva, Estado.Nuevo);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(reserva.getIdReserva()))
                .build();
        return Response.created(location).entity(reserva).build();
    }

    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, Reserva reserva) {
        if (reservaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Reserva con id " + id + " no encontrada"))
                    .build();
        }
        if (reserva == null || reserva.getUsuario() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload de reserva es invalido"))
                    .build();
        }
        reservaBo.guardar(reserva, Estado.Modificado);
        return Response.ok(reserva).build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (reservaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Reserva con id " + id + " no encontrada"))
                    .build();
        }
        reservaBo.eliminar(id);
        return Response.noContent().build();
    }

    @POST
    @Path("{id}/confirmar")
    public Response confirmar(@PathParam("id") int id) {
        if (reservaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Reserva con id " + id + " no encontrada"))
                    .build();
        }
        reservaBo.confirmarReserva(id);
        return Response.ok(Map.of("mensaje", "Reserva confirmada")).build();
    }

    @POST
    @Path("{id}/cancelar")
    public Response cancelar(@PathParam("id") int id) {
        if (reservaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Reserva con id " + id + " no encontrada"))
                    .build();
        }
        reservaBo.cancelarReserva(id);
        return Response.ok(Map.of("mensaje", "Reserva cancelada")).build();
    }

    @GET
    @Path("{id}/comprobante")
    public Response generarComprobante(@PathParam("id") int id) {
        if (reservaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Reserva con id " + id + " no encontrada"))
                    .build();
        }
        ComprobanteCompra comprobante = reservaBo.generarComprobante(id);
        return Response.ok(comprobante).build();
    }

    // RF18 - Resumen de compra antes del pago (asientos, entradas, confitería, total)
    @GET
    @Path("{id}/resumen")
    public Response resumen(@PathParam("id") int id) {
        Reserva reserva = reservaBo.obtener(id);
        if (reserva == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Reserva con id " + id + " no encontrada"))
                    .build();
        }
        ResumenCompra resumen = new ResumenCompra(0, reserva.getTotalFinal(), reserva);
        return Response.ok(resumen).build();
    }

    // RF12 - Tiempo restante del temporizador de reserva (en segundos)
    @GET
    @Path("{id}/tiempo-restante")
    public Response tiempoRestante(@PathParam("id") int id) {
        if (reservaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Reserva con id " + id + " no encontrada"))
                    .build();
        }
        long segundos = reservaBo.obtenerTiempoRestanteSegundos(id);
        return Response.ok(Map.of(
                "segundosRestantes", segundos,
                "expirada", segundos == 0
        )).build();
    }

    // RF12 - Verificar expiración y cancelar automáticamente si corresponde
    @POST
    @Path("{id}/verificar-expiracion")
    public Response verificarExpiracion(@PathParam("id") int id) {
        if (reservaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Reserva con id " + id + " no encontrada"))
                    .build();
        }
        reservaBo.expirarSiCorresponde(id);
        long segundos = reservaBo.obtenerTiempoRestanteSegundos(id);
        return Response.ok(Map.of(
                "expirada", segundos == 0,
                "mensaje", segundos == 0
                        ? "Reserva expirada y cancelada automáticamente"
                        : "Reserva vigente, tiempo restante: " + segundos + "s"
        )).build();
    }
}
