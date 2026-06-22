package pe.edu.pucp.cineflow.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import pe.edu.pucp.cineflow.bo.usuario.IUsuarioBo;
import pe.edu.pucp.cineflow.bo.usuario.UsuarioBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;

import java.util.List;

@WebService(
        serviceName = "UsuariosWS",
        targetNamespace = "http://services.cineflow.pucp.edu.pe/")
public class UsuariosWS {

    private final IUsuarioBo usuarioBo;

    public UsuariosWS() {
        this.usuarioBo = new UsuarioBoImpl();
    }

    @WebMethod(operationName = "listarUsuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioBo.listar();
    }

    @WebMethod(operationName = "obtenerUsuario")
    public Usuario obtenerUsuario(@WebParam(name = "id") int id) {
        return usuarioBo.obtener(id);
    }

    @WebMethod(operationName = "eliminarUsuario")
    public void eliminarUsuario(@WebParam(name = "id") int id) {
        usuarioBo.eliminar(id);
    }

    @WebMethod(operationName = "guardarUsuario")
    public void guardarUsuario(@WebParam(name = "usuario") Usuario usuario,
                               @WebParam(name = "estado") Estado estado) {
        usuarioBo.guardar(usuario, estado);
    }

    @WebMethod(operationName = "obtenerUsuarioPorEmail")
    public Usuario obtenerUsuarioPorEmail(@WebParam(name = "email") String email) {
        return usuarioBo.obtenerPorEmail(email);
    }

    @WebMethod(operationName = "cambiarContrasenia")
    public void cambiarContrasenia(@WebParam(name = "idUsuario") int idUsuario,
                                   @WebParam(name = "contraseniaActual") String contraseniaActual,
                                   @WebParam(name = "nuevaContrasenia") String nuevaContrasenia) {
        usuarioBo.cambiarContrasenia(idUsuario, contraseniaActual, nuevaContrasenia);
    }
}
