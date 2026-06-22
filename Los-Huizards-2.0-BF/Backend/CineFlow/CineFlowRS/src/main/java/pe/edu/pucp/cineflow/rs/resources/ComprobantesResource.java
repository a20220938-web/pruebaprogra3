package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.edu.pucp.cineflow.bo.reporte.ComprobanteBoImpl;
import pe.edu.pucp.cineflow.bo.reporte.IComprobanteBo;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;

import java.util.List;
import java.util.Map;

@Path("/v1/comprobantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComprobantesResource {

    private final IComprobanteBo comprobanteBo;

    public ComprobantesResource() {
        this.comprobanteBo = new ComprobanteBoImpl();
    }

    @GET
    public List<ComprobanteCompra> listar() {
        return comprobanteBo.listar();
    }

    // RF16 - Obtener comprobante por id (incluye código QR)
    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        ComprobanteCompra comprobante = comprobanteBo.obtener(id);
        if (comprobante == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Comprobante con id " + id + " no encontrado"))
                    .build();
        }
        return Response.ok(comprobante).build();
    }

    // RF16 - Obtener comprobante por reserva
    @GET
    @Path("reserva/{idReserva}")
    public Response obtenerPorReserva(@PathParam("idReserva") int idReserva) {
        ComprobanteCompra comprobante = comprobanteBo.obtenerPorReserva(idReserva);
        if (comprobante == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No hay comprobante para la reserva con id " + idReserva))
                    .build();
        }
        return Response.ok(comprobante).build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (comprobanteBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Comprobante con id " + id + " no encontrado"))
                    .build();
        }
        comprobanteBo.eliminar(id);
        return Response.noContent().build();
    }
}
