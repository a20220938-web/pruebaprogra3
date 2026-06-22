package pe.edu.pucp.cineflow.bo.usuario;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;

public interface IUsuarioBo extends IGestionable<Usuario> {
    Usuario obtenerPorEmail(String email);
    void cambiarContrasenia(int idUsuario, String contraseniaActual, String nuevaContrasenia);
    void actualizarPerfil(int idUsuario, String nombre, String apellidos, String telefono, java.time.LocalDate fechaNacimiento);
    Usuario autenticar(String email, String contrasenia);
}
