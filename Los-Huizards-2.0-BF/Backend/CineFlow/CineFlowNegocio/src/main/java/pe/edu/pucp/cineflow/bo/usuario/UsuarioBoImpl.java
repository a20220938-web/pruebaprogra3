package pe.edu.pucp.cineflow.bo.usuario;

import at.favre.lib.crypto.bcrypt.BCrypt;
import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.usuario.IUsuarioDao;
import pe.edu.pucp.cineflow.dao.usuario.UsuarioDaoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;

import java.util.List;

public class UsuarioBoImpl extends BaseBo implements IUsuarioBo {

    private final IUsuarioDao usuarioDao = new UsuarioDaoImpl();

    @Override
    public List<Usuario> listar() {
        return usuarioDao.leerTodos();
    }

    @Override
    public Usuario obtener(int id) {
        validarIdPositivo(id, "id de usuario");
        return usuarioDao.leer(id);
    }

    @Override
    public Usuario obtenerPorEmail(String email) {
        validarTextoObligatorio(email, "email");
        return usuarioDao.leerPorEmail(email);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id de usuario");
        if (!usuarioDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar el usuario con id: " + id);
    }

    @Override
    public void guardar(Usuario modelo, Estado estado) {
        validarUsuario(modelo);
        validarEstado(estado);

        if (estado == Estado.Nuevo) {
            Usuario existente = usuarioDao.leerPorEmail(modelo.getEmail());
            if (existente != null)
                throw new IllegalStateException("Ya existe un usuario con el email: " + modelo.getEmail());
            String hashContrasenia = BCrypt.withDefaults().hashToString(12, modelo.getContrasenia().toCharArray());
            modelo.setContrasenia(hashContrasenia);
            int id = usuarioDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo crear el usuario");
            modelo.setIdUsuario(id);
        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getIdUsuario(), "id de usuario");
            if (!usuarioDao.actualizar(modelo))
                throw new IllegalStateException("No se pudo actualizar el usuario con id: " + modelo.getIdUsuario());
        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }

    @Override
    public void cambiarContrasenia(int idUsuario, String contraseniaActual, String nuevaContrasenia) {
        validarIdPositivo(idUsuario, "id de usuario");
        validarTextoObligatorio(contraseniaActual, "contraseña actual");
        validarTextoObligatorio(nuevaContrasenia, "nueva contraseña");

        Usuario usuario = usuarioDao.leer(idUsuario);
        if (usuario == null)
            throw new IllegalStateException("No existe el usuario con id: " + idUsuario);

        BCrypt.Result resultado = BCrypt.verifyer().verify(contraseniaActual.toCharArray(), usuario.getContrasenia());
        if (!resultado.verified)
            throw new IllegalStateException("La contraseña actual no es correcta");

        String nuevoHash = BCrypt.withDefaults().hashToString(12, nuevaContrasenia.toCharArray());
        usuario.setContrasenia(nuevoHash);
        if (!usuarioDao.actualizar(usuario))
            throw new IllegalStateException("No se pudo actualizar la contraseña");
    }

    @Override
    public void actualizarPerfil(int idUsuario, String nombre, String apellidos, String telefono, java.time.LocalDate fechaNacimiento) {
        validarIdPositivo(idUsuario, "id de usuario");
        validarTextoObligatorio(nombre, "nombre");
        validarTextoObligatorio(apellidos, "apellidos");

        Usuario usuario = usuarioDao.leer(idUsuario);
        if (usuario == null)
            throw new IllegalStateException("No existe el usuario con id: " + idUsuario);

        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setTelefono(telefono);
        usuario.setFechaNacimiento(fechaNacimiento);

        if (!usuarioDao.actualizar(usuario))
            throw new IllegalStateException("No se pudo actualizar el perfil del usuario con id: " + idUsuario);
    }

    @Override
    public Usuario autenticar(String email, String contrasenia) {
        validarTextoObligatorio(email, "email");
        validarTextoObligatorio(contrasenia, "contraseña");

        Usuario usuario = usuarioDao.leerPorEmail(email);
        if (usuario == null)
            return null;

        BCrypt.Result resultado = BCrypt.verifyer().verify(contrasenia.toCharArray(), usuario.getContrasenia());
        if (!resultado.verified)
            return null;

        usuario.setContrasenia(null);
        return usuario;
    }

    private void validarUsuario(Usuario modelo) {
        if (modelo == null)
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        validarTextoObligatorio(modelo.getEmail(), "email");
        validarTextoObligatorio(modelo.getContrasenia(), "contraseña");
        validarTextoObligatorio(modelo.getNombre(), "nombre");
        validarTextoObligatorio(modelo.getApellidos(), "apellidos");
    }
}
