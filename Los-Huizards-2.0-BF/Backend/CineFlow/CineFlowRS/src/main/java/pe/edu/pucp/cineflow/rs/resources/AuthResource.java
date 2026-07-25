package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.edu.pucp.cineflow.bo.usuario.IUsuarioBo;
import pe.edu.pucp.cineflow.bo.usuario.UsuarioBoImpl;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;
import pe.edu.pucp.cineflow.rs.util.EmailService;
import pe.edu.pucp.cineflow.rs.util.TokenStore;

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

    // RF01 - Recuperar contraseña: verifica que el email exista y envía enlace
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
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "El correo ingresado no está registrado"))
                    .build();
        }

        try {
            String token = TokenStore.getInstance().generarToken(email);
            EmailService.enviarResetPassword(email, token);
        } catch (Exception e) {
            String detalle = e.getClass().getSimpleName() + ": " + e.getMessage();
            System.err.println("[AuthResource] Error al enviar correo: " + detalle);
            if (e.getCause() != null) System.err.println("[AuthResource] Causa: " + e.getCause().getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", detalle))
                    .build();
        }

        return Response.ok(Map.of("mensaje", "Enlace de recuperación enviado correctamente al correo ingresado")).build();
    }

    // RF01 - Restablecer contraseña usando el token del enlace
    @POST
    @Path("restablecer-contrasenia")
    public Response restablecerContrasenia(Map<String, String> body) {
        String token = body == null ? null : body.get("token");
        String nuevaContrasenia = body == null ? null : body.get("nuevaContrasenia");

        if (token == null || token.isBlank() || nuevaContrasenia == null || nuevaContrasenia.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Token y nueva contraseña son obligatorios"))
                    .build();
        }

        String email = TokenStore.getInstance().getEmail(token);
        if (email == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "El enlace es inválido o ha expirado"))
                    .build();
        }

        try {
            usuarioBo.restablecerContrasenia(email, nuevaContrasenia);
            TokenStore.getInstance().invalidar(token);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }

        return Response.ok(Map.of("mensaje", "Contraseña restablecida correctamente")).build();
    }
}
