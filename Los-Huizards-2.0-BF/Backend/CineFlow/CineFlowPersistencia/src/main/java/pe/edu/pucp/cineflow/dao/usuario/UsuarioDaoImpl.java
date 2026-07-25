package pe.edu.pucp.cineflow.dao.usuario;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;

import java.sql.*;


public class UsuarioDaoImpl extends DefaultBaseDao<Usuario> implements IUsuarioDao {


    @Override
    public Usuario leerPorEmail(String email) {
        return ejecutarComando(conn -> {
            try (CallableStatement cmd =
                         conn.prepareCall("{call buscarUsuarioPorEmail(?)}")) {
                cmd.setString("p_email", email);
                ResultSet rs = cmd.executeQuery();
                return rs.next() ? mapearModelo(rs) : null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    protected PreparedStatement comandoCrear(Connection conn,
                                             Usuario modelo) throws SQLException {
        CallableStatement cmd = conn.prepareCall(
                "{call insertarUsuario(?, ?, ?, ?, ?, ?, ?, ?)}");
        cmd.setString("p_email",           modelo.getEmail());
        cmd.setString("p_contrasenia",     modelo.getContrasenia());
        cmd.setString("p_nombre",          modelo.getNombre());
        cmd.setString("p_apellidos",       modelo.getApellidos());
        cmd.setString("p_telefono",        modelo.getTelefono());
        if (modelo.getFechaNacimiento() != null) {
            cmd.setDate("p_fechaNacimiento",
                    Date.valueOf(modelo.getFechaNacimiento()));
        } else {
            cmd.setNull("p_fechaNacimiento", Types.DATE);
        }
        cmd.setTimestamp("p_fechaRegistro",
                Timestamp.valueOf(modelo.getFechaRegistro()));
        cmd.registerOutParameter("p_id", Types.INTEGER);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn,
                                                  Usuario modelo) throws SQLException {
        CallableStatement cmd = conn.prepareCall(
                "{call modificarUsuario(?, ?, ?, ?, ?, ?, ?)}");
        cmd.setString("p_email",       modelo.getEmail());
        cmd.setString("p_contrasenia", modelo.getContrasenia());
        cmd.setString("p_nombre",      modelo.getNombre());
        cmd.setString("p_apellidos",   modelo.getApellidos());
        cmd.setString("p_telefono",    modelo.getTelefono());
        if (modelo.getFechaNacimiento() != null) {
            cmd.setDate("p_fechaNacimiento",
                    Date.valueOf(modelo.getFechaNacimiento()));
        } else {
            cmd.setNull("p_fechaNacimiento", Types.DATE);
        }
        cmd.setInt("p_id", modelo.getIdUsuario());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn,
                                                Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call eliminarUsuario(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn,
                                            Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call buscarUsuarioPorId(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareCall("{call listarUsuarios()}");
    }


    @Override
    protected Usuario mapearModelo(ResultSet rs) throws SQLException {
        return new Usuario(
                leerEntero(rs, "idUsuario"),
                leerTexto(rs, "email"),
                leerTexto(rs, "contrasenia"),
                leerTexto(rs, "nombre"),
                leerTexto(rs, "apellidos"),
                leerTexto(rs, "telefono"),
                rs.getDate("fechaNacimiento") != null
                        ? rs.getDate("fechaNacimiento").toLocalDate()
                        : null,
                leerFecha(rs, "fechaRegistro")
        );
    }
}