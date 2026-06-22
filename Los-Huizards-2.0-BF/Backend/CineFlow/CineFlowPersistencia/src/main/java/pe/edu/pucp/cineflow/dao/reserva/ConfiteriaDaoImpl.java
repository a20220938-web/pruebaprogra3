package pe.edu.pucp.cineflow.dao.reserva;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.reserva.ArticuloIndividual;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConfiteriaDaoImpl extends DefaultBaseDao<ArticuloIndividual> implements IConfiteriaDao {

    @Override
    public List<ArticuloIndividual> leerPorInventario(int idInventario) {
        return ejecutarComando(conn -> {
            try {
                List<Object[]> filas = new ArrayList<>();
                try (PreparedStatement cmd = conn.prepareStatement(
                        "SELECT * FROM CONFITERIA WHERE id_inventario = ?;")) {
                    cmd.setInt(1, idInventario);
                    ResultSet rs = cmd.executeQuery();
                    while (rs.next()) {
                        filas.add(new Object[]{
                                leerEntero(rs, "id_item"),
                                leerTexto(rs, "tipo"),
                                leerTexto(rs, "nombre"),
                                leerEntero(rs, "cantidad"),
                                leerDecimal(rs, "precio_unitario")
                        });
                    }
                }

                List<ArticuloIndividual> items = new ArrayList<>(filas.size());
                for (Object[] f : filas) {
                    items.add(new ArticuloIndividual(
                            (int) f[0], (String) f[2], (int) f[3],
                            (double) f[4], (String) f[1], idInventario));
                }
                return items;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected PreparedStatement comandoCrear(Connection conn, ArticuloIndividual modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "INSERT INTO CONFITERIA (tipo, nombre, cantidad, precio_unitario, id_inventario) " +
                "VALUES (?, ?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS);
        cmd.setString(1, modelo.getCategoria());  // tipo en SQL = Categoria en modelo
        cmd.setString(2, modelo.getNombre());
        cmd.setInt(3, modelo.getCantidad());
        cmd.setDouble(4, modelo.getPrecioUnitario());
        cmd.setInt(5, modelo.getIdInventario()); // TODO: modelo.getIdInventario() cuando se agregue al modelo
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, ArticuloIndividual modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "UPDATE CONFITERIA SET tipo = ?, nombre = ?, cantidad = ?, precio_unitario = ? " +
                "WHERE id_item = ?;");
        cmd.setString(1, modelo.getCategoria());
        cmd.setString(2, modelo.getNombre());
        cmd.setInt(3, modelo.getCantidad());
        cmd.setDouble(4, modelo.getPrecioUnitario());
        cmd.setInt(5, modelo.getIdItem());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement("DELETE FROM CONFITERIA WHERE id_item = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement("SELECT * FROM CONFITERIA WHERE id_item = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareStatement("SELECT * FROM CONFITERIA;");
    }

    @Override
    protected ArticuloIndividual mapearModelo(ResultSet rs) throws SQLException {
        return new ArticuloIndividual(
                leerEntero(rs, "id_item"),
                leerTexto(rs, "nombre"),
                leerEntero(rs, "cantidad"),
                leerDecimal(rs, "precio_unitario"),
                leerTexto(rs, "tipo"),
                leerEntero(rs, "id_inventario")
        );
    }
}
