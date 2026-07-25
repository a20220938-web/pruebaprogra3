package pe.edu.pucp.cineflow.dao.catalogo;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.catalogo.Sala;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDaoImpl extends DefaultBaseDao<Sala> implements ISalaDao {

    @Override
    protected PreparedStatement comandoCrear(Connection conn, Sala modelo) throws SQLException {
        if (modelo.getCine() == null) {
            throw new IllegalArgumentException("El cine es obligatorio para crear una sala");
        }
        PreparedStatement cmd = conn.prepareStatement(
                "INSERT INTO SALA (numero, capacidad, id_cine) VALUES (?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS);
        cmd.setInt(1, modelo.getNumero());
        cmd.setInt(2, modelo.getCapacidad());
        cmd.setInt(3, modelo.getCine().getId());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, Sala modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "UPDATE SALA SET numero = ?, capacidad = ? WHERE id_sala = ?;");
        cmd.setInt(1, modelo.getNumero());
        cmd.setInt(2, modelo.getCapacidad());
        cmd.setInt(3, modelo.getId());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement("DELETE FROM SALA WHERE id_sala = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement("SELECT * FROM SALA WHERE id_sala = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareStatement("SELECT * FROM SALA;");
    }

    @Override
    protected Sala mapearModelo(ResultSet rs) throws SQLException {
        int idCine = leerEntero(rs, "id_cine");
        return new Sala(
                leerEntero(rs, "id_sala"),
                leerEntero(rs, "numero"),
                leerEntero(rs, "capacidad"),
                0,
                0,
                new CineDaoImpl().leer(idCine)
        );
    }

    @Override
    public List<Sala> listarPorCine(int idCine) {
        return ejecutarComando(conn -> {
            List<Sala> salas = new ArrayList<>();
            try (PreparedStatement cmd = conn.prepareStatement("SELECT * FROM SALA WHERE id_cine = ?;")) {
                cmd.setInt(1, idCine);
                try (ResultSet rs = cmd.executeQuery()) {
                    while (rs.next()) {
                        salas.add(mapearModelo(rs));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return salas;
        });
    }
}
