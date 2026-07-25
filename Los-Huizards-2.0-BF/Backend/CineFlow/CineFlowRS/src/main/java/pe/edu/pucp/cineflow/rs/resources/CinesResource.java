package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pe.edu.pucp.cineflow.bo.catalogo.CineBoImpl;
import pe.edu.pucp.cineflow.bo.catalogo.ICineBo;
import pe.edu.pucp.cineflow.bo.catalogo.ISalaBo;
import pe.edu.pucp.cineflow.bo.catalogo.SalaBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Cine;
import pe.edu.pucp.cineflow.modelo.catalogo.Sala;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/v1/cines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CinesResource {

    private final ICineBo cineBo;
    private final ISalaBo salaBo;

    @Context
    private UriInfo uriInfo;

    public CinesResource() {
        this.cineBo = new CineBoImpl();
        this.salaBo = new SalaBoImpl();
    }

    @GET
    public List<Cine> listar() {
        return cineBo.listar();
    }

    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        Cine cine = cineBo.obtener(id);
        if (cine == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Cine con id " + id + " no encontrado"))
                    .build();
        }
        return Response.ok(cine).build();
    }

    @GET
    @Path("{id}/salas")
    public Response listarSalas(@PathParam("id") int id) {
        if (cineBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Cine con id " + id + " no encontrado"))
                    .build();
        }
        List<Sala> salas = salaBo.listarPorCine(id);
        return Response.ok(salas).build();
    }

    @POST
    public Response crear(Cine cine) {
        if (cine == null || cine.getNombre() == null || cine.getNombre().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload del cine es invalido"))
                    .build();
        }
        cineBo.guardar(cine, Estado.Nuevo);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(cine.getId()))
                .build();
        return Response.created(location).entity(cine).build();
    }

    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, Cine cine) {
        if (cineBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Cine con id " + id + " no encontrado"))
                    .build();
        }
        if (cine == null || cine.getNombre() == null || cine.getNombre().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload del cine es invalido"))
                    .build();
        }
        cine.setId(id);
        cineBo.guardar(cine, Estado.Modificado);
        return Response.ok(cine).build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (cineBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Cine con id " + id + " no encontrado"))
                    .build();
        }
        cineBo.eliminar(id);
        return Response.noContent().build();
    }
}
