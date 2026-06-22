package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.edu.pucp.cineflow.bo.usuario.IUsuarioBo;
import pe.edu.pucp.cineflow.bo.usuario.UsuarioBoImpl;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;

import java.util.Map;

@Path("/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final IUsuarioBo usuarioBo;

    public AuthResource() {
        this.usuarioBo = new UsuarioBoImpl();
    }

    // RF01 - Iniciar sesión
    @POST
    @Path("login")
    public Response login(Map<String, String> credenciales) {
        String email = credenciales == null ? null : credenciales.get("email");
        String contrasenia = credenciales == null ? null : credenciales.get("contrasenia");

        if (email == null || email.isBlank() || contrasenia == null || contrasenia.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Email y contraseña son obligatorios"))
                    .build();
        }

        Usuario usuario = usuarioBo.autenticar(email, contrasenia);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Credenciales inválidas"))
                    .build();
        }

        return Response.ok(usuario).build();
    }

    // RF01 - Cerrar sesión (stateless: el cliente descarta el token/sesión)
    @POST
    @Path("logout")
    public Response logout() {
        return Response.ok(Map.of("mensaje", "Sesión cerrada correctamente")).build();
    }

    // RF01 - Recuperar contraseña: verifica que el email exista
    @POST
    @Path("recuperar-contrasenia")
    public Response recuperarContrasenia(Map<String, String> body) {
        String email = body == null ? null : body.get("email");

        if (email == null || email.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El email es obligatorio"))
                    .build();
        }

        Usuario usuario = usuarioBo.obtenerPorEmail(email);
        // Siempre 200 para no revelar si el email existe
        if (usuario != null) {
            // En producción: aquí se enviaría el enlace de recuperación al correo
        }
        return Response.ok(Map.of("mensaje", "Si el email existe, recibirás un enlace de recuperación")).build();
    }
}
