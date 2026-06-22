package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pe.edu.pucp.cineflow.bo.reserva.IInventarioCineBo;
import pe.edu.pucp.cineflow.bo.reserva.InventarioCineBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reserva.InventarioCine;

import java.util.List;
import java.util.Map;

@Path("/v1/inventario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InventarioResource {

    private final IInventarioCineBo inventarioBo;

    @Context
    private UriInfo uriInfo;

    public InventarioResource() {
        this.inventarioBo = new InventarioCineBoImpl();
    }

    @GET
    public List<InventarioCine> listar() {
        return inventarioBo.listar();
    }

    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        InventarioCine inventario = inventarioBo.obtener(id);
        if (inventario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Inventario con id " + id + " no encontrado"))
                    .build();
        }
        return Response.ok(inventario).build();
    }

    // RF10 - Inventario del cine con stock actual
    @GET
    @Path("cine/{idCine}")
    public Response obtenerPorCine(@PathParam("idCine") int idCine) {
        InventarioCine inventario = inventarioBo.obtenerPorCine(idCine);
        if (inventario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No se encontró inventario para el cine con id " + idCine))
                    .build();
        }
        return Response.ok(inventario).build();
    }

    // RF10 - Verificar disponibilidad de stock en tiempo real
    @GET
    @Path("{id}/disponibilidad")
    public Response verificarDisponibilidad(
            @PathParam("id") int id,
            @QueryParam("cantidad") @DefaultValue("1") int cantidad) {

        InventarioCine inventario = inventarioBo.obtener(id);
        if (inventario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Inventario con id " + id + " no encontrado"))
                    .build();
        }

        boolean disponible = inventarioBo.verificarDisponibilidad(id, cantidad);
        return Response.ok(Map.of(
                "disponible", disponible,
                "stockActual", inventario.getStockActual(),
                "cantidadSolicitada", cantidad
        )).build();
    }

    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, InventarioCine inventario) {
        if (inventarioBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Inventario con id " + id + " no encontrado"))
                    .build();
        }
        inventario.setIdInventario(id);
        inventarioBo.guardar(inventario, Estado.Modificado);
        return Response.ok(inventario).build();
    }
}
