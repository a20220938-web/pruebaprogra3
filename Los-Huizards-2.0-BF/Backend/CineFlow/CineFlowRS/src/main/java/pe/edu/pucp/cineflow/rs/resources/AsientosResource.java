package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.edu.pucp.cineflow.bo.reserva.AsientoBoImpl;
import pe.edu.pucp.cineflow.bo.reserva.IAsientoBo;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;

import java.util.List;
import java.util.Map;

@Path("/v1/asientos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AsientosResource {

    private final IAsientoBo asientoBo;

    public AsientosResource() {
        this.asientoBo = new AsientoBoImpl();
    }

    @GET
    public List<Asiento> listar() {
        return asientoBo.listar();
    }

    // RF07 - Detalle de un asiento con su estado actual
    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        Asiento asiento = asientoBo.obtener(id);
        if (asiento == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Asiento con id " + id + " no encontrado"))
                    .build();
        }
        return Response.ok(asiento).build();
    }

    // RF08 - Cambiar estado de un asiento: body { "estado": "RESERVADO" | "DISPONIBLE" | "OCUPADO" }
    @PUT
    @Path("{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, Map<String, String> body) {
        if (body == null || body.get("estado") == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El campo 'estado' es obligatorio"))
                    .build();
        }

        EstadoAsiento nuevoEstado;
        try {
            nuevoEstado = EstadoAsiento.valueOf(body.get("estado").toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Estado inválido: " + body.get("estado") + ". Valores permitidos: DISPONIBLE, RESERVADO, OCUPADO"))
                    .build();
        }

        if (asientoBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Asiento con id " + id + " no encontrado"))
                    .build();
        }

        try {
            asientoBo.cambiarEstado(id, nuevoEstado);
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }

        return Response.ok(Map.of("mensaje", "Estado del asiento actualizado a " + nuevoEstado)).build();
    }
}
