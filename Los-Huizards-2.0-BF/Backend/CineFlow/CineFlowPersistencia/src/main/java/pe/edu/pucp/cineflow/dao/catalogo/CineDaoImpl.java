package pe.edu.pucp.cineflow.dao.catalogo;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.catalogo.Cartelera;
import pe.edu.pucp.cineflow.modelo.catalogo.Cine;

import java.sql.*;
import java.util.ArrayList;

public class CineDaoImpl extends DefaultBaseDao<Cine> implements ICineDao {

    @Override
    protected PreparedStatement comandoCrear(Connection conn, Cine modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "INSERT INTO CINE (nombre, ubicacion) VALUES (?, ?);",
                Statement.RETURN_GENERATED_KEYS);
        cmd.setString(1, modelo.getNombre());
        cmd.setString(2, modelo.getUbicacion());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, Cine modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "UPDATE CINE SET nombre = ?, ubicacion = ? WHERE id_cine = ?;");
        cmd.setString(1, modelo.getNombre());
        cmd.setString(2, modelo.getUbicacion());
        cmd.setInt(3, modelo.getId());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "DELETE FROM CINE WHERE id_cine = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "SELECT * FROM CINE WHERE id_cine = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareStatement("SELECT * FROM CINE;");
    }

    @Override
    protected Cine mapearModelo(ResultSet rs) throws SQLException {
        return new Cine(
                leerEntero(rs, "id_cine"),
                leerTexto(rs, "nombre"),
                leerTexto(rs, "ubicacion"),
                new Cartelera()
        );
    }
}
