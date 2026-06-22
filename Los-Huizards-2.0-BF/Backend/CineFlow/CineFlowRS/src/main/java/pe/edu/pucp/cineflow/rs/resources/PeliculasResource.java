package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pe.edu.pucp.cineflow.bo.catalogo.FuncionBoImpl;
import pe.edu.pucp.cineflow.bo.catalogo.IFuncionBo;
import pe.edu.pucp.cineflow.bo.catalogo.IPeliculaBo;
import pe.edu.pucp.cineflow.bo.catalogo.PeliculaBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;
import pe.edu.pucp.cineflow.modelo.catalogo.Genero;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/v1/peliculas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeliculasResource {

    private final IPeliculaBo peliculaBo;
    private final IFuncionBo funcionBo;

    @Context
    private UriInfo uriInfo;

    public PeliculasResource() {
        this.peliculaBo = new PeliculaBoImpl();
        this.funcionBo = new FuncionBoImpl();
    }

    // RF04/RF05 - Cartelera con búsqueda y filtrado opcional
    @GET
    public Response listar(
            @QueryParam("nombre") String nombre,
            @QueryParam("cine") String cine,
            @QueryParam("fecha") String fecha,
            @QueryParam("genero") String genero) {

        LocalDate fechaFiltro = null;
        if (fecha != null && !fecha.isBlank()) {
            try {
                fechaFiltro = LocalDate.parse(fecha);
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Formato de fecha inválido. Use YYYY-MM-DD"))
                        .build();
            }
        }

        Genero generoFiltro = null;
        if (genero != null && !genero.isBlank()) {
            try {
                generoFiltro = Genero.valueOf(genero.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Género inválido: " + genero))
                        .build();
            }
        }

        boolean hayFiltros = (nombre != null && !nombre.isBlank())
                || (cine != null && !cine.isBlank())
                || fechaFiltro != null
                || generoFiltro != null;

        List<Pelicula> resultado = hayFiltros
                ? peliculaBo.buscar(nombre, cine, fechaFiltro, generoFiltro)
                : peliculaBo.listar();

        return Response.ok(resultado).build();
    }

    // RF06 - Detalle de película
    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        Pelicula pelicula = peliculaBo.obtener(id);
        if (pelicula == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pelicula con id " + id + " no encontrada"))
                    .build();
        }
        return Response.ok(pelicula).build();
    }

    // RF06 - Horarios/funciones disponibles para una película
    @GET
    @Path("{id}/funciones")
    public Response listarFunciones(@PathParam("id") int id) {
        if (peliculaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pelicula con id " + id + " no encontrada"))
                    .build();
        }
        List<Funcion> funciones = funcionBo.listarPorPelicula(id);
        return Response.ok(funciones).build();
    }

    @POST
    public Response crear(Pelicula pelicula) {
        if (pelicula == null || pelicula.getTitulo() == null || pelicula.getTitulo().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload de pelicula es invalido"))
                    .build();
        }
        peliculaBo.guardar(pelicula, Estado.Nuevo);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(pelicula.getId()))
                .build();
        return Response.created(location).entity(pelicula).build();
    }

    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, Pelicula pelicula) {
        if (peliculaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pelicula con id " + id + " no encontrada"))
                    .build();
        }
        if (pelicula == null || pelicula.getTitulo() == null || pelicula.getTitulo().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload de pelicula es invalido"))
                    .build();
        }
        peliculaBo.guardar(pelicula, Estado.Modificado);
        return Response.ok(pelicula).build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (peliculaBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Pelicula con id " + id + " no encontrada"))
                    .build();
        }
        peliculaBo.eliminar(id);
        return Response.noContent().build();
    }
}
