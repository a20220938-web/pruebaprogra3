package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import pe.edu.pucp.cineflow.bo.reserva.IReservaBo;
import pe.edu.pucp.cineflow.bo.reserva.ReservaBoImpl;
import pe.edu.pucp.cineflow.bo.usuario.IUsuarioBo;
import pe.edu.pucp.cineflow.bo.usuario.UsuarioBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/v1/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuariosResource {

    private final IUsuarioBo usuarioBo;
    private final IReservaBo reservaBo;

    @Context
    private UriInfo uriInfo;

    public UsuariosResource() {
        this.usuarioBo = new UsuarioBoImpl();
        this.reservaBo = new ReservaBoImpl();
    }

    @GET
    public List<Usuario> listar() {
        return usuarioBo.listar();
    }

    @GET
    @Path("{id}")
    public Response obtener(@PathParam("id") int id) {
        Usuario usuario = usuarioBo.obtener(id);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Usuario con id " + id + " no encontrado"))
                    .build();
        }
        return Response.ok(usuario).build();
    }

    @GET
    @Path("email/{email}")
    public Response obtenerPorEmail(@PathParam("email") String email) {
        Usuario usuario = usuarioBo.obtenerPorEmail(email);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Usuario con email " + email + " no encontrado"))
                    .build();
        }
        return Response.ok(usuario).build();
    }

    // RF03 - Historial de compras del usuario
    @GET
    @Path("{id}/historial")
    public Response historial(@PathParam("id") int id) {
        if (usuarioBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Usuario con id " + id + " no encontrado"))
                    .build();
        }
        List<Reserva> reservas = reservaBo.listarPorUsuario(id);
        return Response.ok(reservas).build();
    }

    // RF01 - Registro de usuario
    @POST
    public Response crear(Usuario usuario) {
        if (usuario == null || usuario.getEmail() == null || usuario.getEmail().isBlank()
                || usuario.getContrasenia() == null || usuario.getContrasenia().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload del usuario es invalido"))
                    .build();
        }
        usuarioBo.guardar(usuario, Estado.Nuevo);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(usuario.getIdUsuario()))
                .build();
        return Response.created(location).entity(usuario).build();
    }

    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, Usuario usuario) {
        if (usuarioBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Usuario con id " + id + " no encontrado"))
                    .build();
        }
        if (usuario == null || usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El payload del usuario es invalido"))
                    .build();
        }
        usuarioBo.guardar(usuario, Estado.Modificado);
        return Response.ok(usuario).build();
    }

    // RF02 - Editar perfil (solo datos personales, sin tocar email ni contraseña)
    @PUT
    @Path("{id}/perfil")
    public Response actualizarPerfil(@PathParam("id") int id, Map<String, String> datos) {
        if (usuarioBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Usuario con id " + id + " no encontrado"))
                    .build();
        }
        if (datos == null || datos.get("nombre") == null || datos.get("apellidos") == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "nombre y apellidos son obligatorios"))
                    .build();
        }

        String nombre = datos.get("nombre");
        String apellidos = datos.get("apellidos");
        String telefono = datos.get("telefono");
        LocalDate fechaNacimiento = datos.containsKey("fechaNacimiento") && datos.get("fechaNacimiento") != null
                ? LocalDate.parse(datos.get("fechaNacimiento"))
                : null;

        usuarioBo.actualizarPerfil(id, nombre, apellidos, telefono, fechaNacimiento);
        return Response.ok(Map.of("mensaje", "Perfil actualizado correctamente")).build();
    }

    // RF01 - Cambiar contraseña
    @PUT
    @Path("{id}/contrasenia")
    public Response cambiarContrasenia(@PathParam("id") int id, Map<String, String> datos) {
        if (datos == null || datos.get("contraseniaActual") == null || datos.get("nuevaContrasenia") == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "contraseniaActual y nuevaContrasenia son obligatorios"))
                    .build();
        }
        try {
            usuarioBo.cambiarContrasenia(id, datos.get("contraseniaActual"), datos.get("nuevaContrasenia"));
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
        return Response.ok(Map.of("mensaje", "Contraseña actualizada correctamente")).build();
    }

    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        if (usuarioBo.obtener(id) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Usuario con id " + id + " no encontrado"))
                    .build();
        }
        usuarioBo.eliminar(id);
        return Response.noContent().build();
    }
}
