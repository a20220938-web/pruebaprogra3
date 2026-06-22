package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pe.edu.pucp.cineflow.bo.catalogo.FuncionBoImpl;
import pe.edu.pucp.cineflow.bo.catalogo.IFuncionBo;
import pe.edu.pucp.cineflow.bo.reserva.AsientoBoImpl;
import pe.edu.pucp.cineflow.bo.reserva.IAsientoBo;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.Entrada;
import pe.edu.pucp.cineflow.modelo.reserva.tipoEntrada;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/v1/funciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FuncionesResource {

    private final IFuncionBo funcionBo;
    private final IAsientoBo asientoBo;

    @Context
    private UriInfo uriInfo;

    public FuncionesResource() {
        this.funcionBo = new FuncionBoImpl();
        this.asientoBo = new AsientoBoImpl();
    }

    @GET
    public List<Funcion> listar() {
        return funcionBo.listar();
    }

    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        Funcion funcion = funcionBo.obtener(id);
        if (funcion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Funcion con id " + id + " no encontrada"))
                    .build();
        }
        return Response.ok(funcion).build();
    }

    @GET
    @Path("pelicula/{idPelicula}")
    public Response listarPorPelicula(@PathParam("idPelicula") int idPelicula) {
        List<Funcion> funciones = funcionBo.listarPorPelicula(idPelicula);
        if (funciones == null || funciones.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No hay funciones para la pelicula con id " + idPelicula))
                    .build();
        }
        return Response.ok(funciones).build();
    }

    // RF07 - Mapa de asientos de una función (con su estado: DISPONIBLE/RESERVADO/OCUPADO)
    @GET
    @Path("{id}/asientos")
    public Response listarAsientos(@PathParam("id") int id) {
        if (funcionBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Función con id " + id + " no encontrada"))
                    .build();
        }
        List<Asiento> asientos = asientoBo.listarPorFuncion(id);
        return Response.ok(asientos).build();
    }

    // RF09 - Tipos de entrada disponibles con precio calculado según el precio base de la función
    @GET
    @Path("{id}/entradas")
    public Response listarTiposEntrada(@PathParam("id") int id) {
        Funcion funcion = funcionBo.obtener(id);
        if (funcion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Función con id " + id + " no encontrada"))
                    .build();
        }
        double precioBase = funcion.getPrecioBase();
        List<Map<String, Object>> tipos = Arrays.stream(tipoEntrada.values())
                .map(tipo -> {
                    Entrada entrada = new Entrada(0, precioBase, tipo);
                    return Map.<String, Object>of(
                            "tipo", tipo.name(),
                            "precioBase", precioBase,
                            "precioFinal", entrada.calcularPrecio(),
                            "descuento", tipo == tipoEntrada.NINO ? "30%" :
                                         tipo == tipoEntrada.CONADIS ? "50%" : "0%"
                    );
                })
                .collect(Collectors.toList());
        return Response.ok(tipos).build();
    }

    @POST
    public Response crear(Funcion funcion) {
        if (funcion == null || funcion.getPelicula() == null || funcion.getSala() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload de funcion es invalido"))
                    .build();
        }
        funcionBo.guardar(funcion, Estado.Nuevo);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(funcion.getId()))
                .build();
        return Response.created(location).entity(funcion).build();
    }

    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, Funcion funcion) {
        if (funcionBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Funcion con id " + id + " no encontrada"))
                    .build();
        }
        if (funcion == null || funcion.getPelicula() == null || funcion.getSala() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload de funcion es invalido"))
                    .build();
        }
        funcionBo.guardar(funcion, Estado.Modificado);
        return Response.ok(funcion).build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (funcionBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Funcion con id " + id + " no encontrada"))
                    .build();
        }
        funcionBo.eliminar(id);
        return Response.noContent().build();
    }
}
