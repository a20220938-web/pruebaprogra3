package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pe.edu.pucp.cineflow.bo.pago.IPagoBo;
import pe.edu.pucp.cineflow.bo.pago.PagoBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.pago.EstadoPago;
import pe.edu.pucp.cineflow.modelo.pago.MetodoPago;
import pe.edu.pucp.cineflow.modelo.pago.Pago;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;
import pe.edu.pucp.cineflow.rs.util.StripeService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Path("/v1/pagos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagosResource {

    private final IPagoBo pagoBo;

    @Context
    private UriInfo uriInfo;

    public PagosResource() {
        this.pagoBo = new PagoBoImpl();
    }

    @GET
    public List<Pago> listar() {
        return pagoBo.listar();
    }

    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        Pago pago = pagoBo.obtener(id);
        if (pago == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pago con id " + id + " no encontrado"))
                    .build();
        }
        return Response.ok(pago).build();
    }

    // RF13 - Métodos de pago disponibles (TARJETA, BILLETERA_DIGITAL)
    @GET
    @Path("metodos")
    public Response listarMetodos() {
        return Response.ok(pagoBo.listarMetodosPago()).build();
    }

    // RF13 - Registrar y procesar un pago
    @POST
    public Response crear(Pago pago) {
        if (pago == null || pago.getMonto() <= 0 || pago.getMetodo() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "monto (>0) y metodo son obligatorios"))
                    .build();
        }

        // Validar método de pago
        try {
            MetodoPago.valueOf(pago.getMetodo().name());
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Método de pago inválido. Use: " + pagoBo.listarMetodosPago()))
                    .build();
        }

        pago.setFecha(LocalDateTime.now());
        pago.setEstado(EstadoPago.PENDIENTE);
        pagoBo.guardar(pago, Estado.Nuevo);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(pago.getIdPago()))
                .build();
        return Response.created(location).entity(pago).build();
    }

    // RF13 - Procesar pago con Stripe (tarjeta) o simular (Yape/Plin)
    @POST
    @Path("{id}/procesar")
    public Response procesar(@PathParam("id") int id, Map<String, String> body) {
        Pago pago = pagoBo.obtener(id);
        if (pago == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pago con id " + id + " no encontrado"))
                    .build();
        }
        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Solo se puede procesar un pago en estado PENDIENTE"))
                    .build();
        }

        boolean aprobado;
        String metodoPago = body != null ? body.getOrDefault("metodoPago", "TARJETA") : "TARJETA";
        String cardNumber = body != null ? body.getOrDefault("cardNumber", "") : "";

        try {
            if ("TARJETA".equals(metodoPago)) {
                aprobado = StripeService.cobrar(pago.getMonto(), "CineFlow reserva #" + id, cardNumber);
            } else {
                aprobado = StripeService.cobrarBilleteraDigital(pago.getMonto());
            }
        } catch (Exception e) {
            System.err.println("[PagosResource] Error Stripe: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error al procesar el pago: " + e.getMessage()))
                    .build();
        }

        pago.actualizarEstado(aprobado);
        pagoBo.guardar(pago, Estado.Modificado);

        return Response.ok(Map.of(
                "aprobado", aprobado,
                "estado", pago.getEstado().name(),
                "mensaje", aprobado ? "Pago aprobado" : "Pago rechazado"
        )).build();
    }

    // RF14 - Confirmar transacción aprobada: reserva, asientos, stock y QR
    @POST
    @Path("{id}/confirmar")
    public Response confirmar(@PathParam("id") int id) {
        Pago pago = pagoBo.obtener(id);
        if (pago == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pago con id " + id + " no encontrado"))
                    .build();
        }

        try {
            ComprobanteCompra comprobante = pagoBo.confirmarTransaccion(id);
            return Response.ok(comprobante).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // RF14 - Aprobar pago manualmente (respuesta de proveedor externo)
    @POST
    @Path("{id}/aprobar")
    public Response aprobar(@PathParam("id") int id) {
        if (pagoBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pago con id " + id + " no encontrado"))
                    .build();
        }
        try {
            pagoBo.aprobarPago(id);
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
        return Response.ok(Map.of("mensaje", "Pago aprobado")).build();
    }

    // RF15 - Rechazar pago y revertir transacción (libera asientos y repone stock)
    @POST
    @Path("{id}/rechazar")
    public Response rechazar(@PathParam("id") int id) {
        if (pagoBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pago con id " + id + " no encontrado"))
                    .build();
        }
        try {
            pagoBo.rechazarPago(id);
            pagoBo.revertirTransaccion(id);
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
        return Response.ok(Map.of("mensaje", "Pago rechazado. Asientos liberados y stock repuesto")).build();
    }

    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, Pago pago) {
        if (pagoBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pago con id " + id + " no encontrado"))
                    .build();
        }
        pago.setIdPago(id);
        pagoBo.guardar(pago, Estado.Modificado);
        return Response.ok(pago).build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (pagoBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pago con id " + id + " no encontrado"))
                    .build();
        }
        pagoBo.eliminar(id);
        return Response.noContent().build();
    }
}
