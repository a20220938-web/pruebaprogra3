package pe.edu.pucp.cineflow.dao.reserva;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.reserva.InventarioCine;

import java.sql.*;

public class InventarioCineDaoImpl extends DefaultBaseDao<InventarioCine>
        implements IInventarioCineDao {

    @Override
    public InventarioCine leerPorCine(int idCine) {
        return ejecutarComando(conn -> {
            try (PreparedStatement cmd = conn.prepareStatement(
                    "SELECT * FROM inventario_cine WHERE id_cine = ? LIMIT 1;")) {
                cmd.setInt(1, idCine);
                ResultSet rs = cmd.executeQuery();
                return rs.next() ? mapearModelo(rs) : null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected PreparedStatement comandoCrear(Connection conn, InventarioCine modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "INSERT INTO inventario_cine (stock_actual, stock_minimo, ultima_reposicion, id_cine) " +
                        "VALUES (?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS);
        cmd.setInt(1, modelo.getStockActual());
        cmd.setInt(2, modelo.getStockMinimo());
        cmd.setTimestamp(3, modelo.getUltimaReposicion() != null
                ? Timestamp.valueOf(modelo.getUltimaReposicion()) : null);
        cmd.setInt(4, modelo.getIdCine());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, InventarioCine modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "UPDATE inventario_cine SET stock_actual = ?, stock_minimo = ?, " +
                        "ultima_reposicion = ? WHERE id_inventario = ?;");
        cmd.setInt(1, modelo.getStockActual());
        cmd.setInt(2, modelo.getStockMinimo());
        cmd.setTimestamp(3, modelo.getUltimaReposicion() != null
                ? Timestamp.valueOf(modelo.getUltimaReposicion()) : null);
        cmd.setInt(4, modelo.getIdInventario());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "DELETE FROM inventario_cine WHERE id_inventario = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "SELECT * FROM inventario_cine WHERE id_inventario = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareStatement("SELECT * FROM inventario_cine;");
    }

    @Override
    protected InventarioCine mapearModelo(ResultSet rs) throws SQLException {
        InventarioCine inv = new InventarioCine();
        inv.setIdInventario(leerEntero(rs, "id_inventario"));
        inv.setStockActual(leerEntero(rs, "stock_actual"));
        inv.setStockMinimo(leerEntero(rs, "stock_minimo"));
        Timestamp ts = rs.getTimestamp("ultima_reposicion");
        inv.setUltimaReposicion(ts != null ? ts.toLocalDateTime() : null);
        inv.setIdCine(leerEntero(rs, "id_cine"));
        return inv;
    }
}