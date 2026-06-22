package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pe.edu.pucp.cineflow.bo.reserva.ConfiteriaBoImpl;
import pe.edu.pucp.cineflow.bo.reserva.IConfiteriaBo;
import pe.edu.pucp.cineflow.bo.reserva.IInventarioCineBo;
import pe.edu.pucp.cineflow.bo.reserva.InventarioCineBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reserva.ArticuloIndividual;
import pe.edu.pucp.cineflow.modelo.reserva.InventarioCine;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/v1/confiteria")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfiteriaResource {

    private final IConfiteriaBo confiteriaBo;
    private final IInventarioCineBo inventarioBo;

    @Context
    private UriInfo uriInfo;

    public ConfiteriaResource() {
        this.confiteriaBo = new ConfiteriaBoImpl();
        this.inventarioBo = new InventarioCineBoImpl();
    }

    @GET
    public List<ArticuloIndividual> listar() {
        return confiteriaBo.listar();
    }

    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        ArticuloIndividual articulo = confiteriaBo.obtener(id);
        if (articulo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Articulo con id " + id + " no encontrado"))
                    .build();
        }
        return Response.ok(articulo).build();
    }

    @GET
    @Path("inventario/{idInventario}")
    public Response listarPorInventario(@PathParam("idInventario") int idInventario) {
        List<ArticuloIndividual> articulos = confiteriaBo.listarPorInventario(idInventario);
        if (articulos == null || articulos.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No hay articulos para el inventario con id " + idInventario))
                    .build();
        }
        return Response.ok(articulos).build();
    }

    // RF10 - Catálogo de confitería por cine (obtiene inventario del cine y lista sus artículos)
    @GET
    @Path("cine/{idCine}")
    public Response listarPorCine(@PathParam("idCine") int idCine) {
        InventarioCine inventario = inventarioBo.obtenerPorCine(idCine);
        if (inventario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No se encontró inventario para el cine con id " + idCine))
                    .build();
        }
        List<ArticuloIndividual> articulos = confiteriaBo.listarPorInventario(inventario.getIdInventario());
        return Response.ok(articulos).build();
    }

    @POST
    public Response crear(ArticuloIndividual articulo) {
        if (articulo == null || articulo.getNombre() == null || articulo.getNombre().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload del articulo es invalido"))
                    .build();
        }
        if (articulo.getIdInventario() <= 0) {
            List<InventarioCine> inventarios = inventarioBo.listar();
            if (inventarios == null || inventarios.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "No existe ningún inventario de cine registrado"))
                        .build();
            }
            articulo.setIdInventario(inventarios.get(0).getIdInventario());
        }
        confiteriaBo.guardar(articulo, Estado.Nuevo);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(articulo.getIdItem()))
                .build();
        return Response.created(location).entity(articulo).build();
    }

    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, ArticuloIndividual articulo) {
        if (confiteriaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Articulo con id " + id + " no encontrado"))
                    .build();
        }
        if (articulo == null || articulo.getNombre() == null || articulo.getNombre().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload del articulo es invalido"))
                    .build();
        }
        confiteriaBo.guardar(articulo, Estado.Modificado);
        return Response.ok(articulo).build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (confiteriaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Articulo con id " + id + " no encontrado"))
                    .build();
        }
        confiteriaBo.eliminar(id);
        return Response.noContent().build();
    }
}
