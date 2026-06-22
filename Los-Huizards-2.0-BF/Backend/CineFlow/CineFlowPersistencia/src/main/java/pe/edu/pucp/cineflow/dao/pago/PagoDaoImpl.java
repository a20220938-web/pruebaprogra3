package pe.edu.pucp.cineflow.dao.pago;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.pago.EstadoPago;
import pe.edu.pucp.cineflow.modelo.pago.MetodoPago;
import pe.edu.pucp.cineflow.modelo.pago.Pago;

import java.sql.*;


public class PagoDaoImpl extends DefaultBaseDao<Pago> implements IPagoDao {

    @Override
    protected PreparedStatement comandoCrear(Connection conn,
                                             Pago modelo) throws SQLException {
        CallableStatement cmd = conn.prepareCall(
                "{call insertarPago(?, ?, ?, ?, ?)}");
        cmd.setDouble("p_monto",  modelo.getMonto());
        cmd.setTimestamp("p_fecha", Timestamp.valueOf(modelo.getFecha()));
        cmd.setString("p_estado", modelo.getEstado().name());
        cmd.setString("p_metodo", modelo.getMetodo().name());
        cmd.registerOutParameter("p_id", Types.INTEGER);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn,
                                                  Pago modelo) throws SQLException {
        CallableStatement cmd = conn.prepareCall(
                "{call modificarPago(?, ?, ?)}");
        cmd.setString("p_estado", modelo.getEstado().name());
        cmd.setDouble("p_monto",  modelo.getMonto());
        cmd.setInt("p_id",        modelo.getIdPago());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn,
                                                Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call eliminarPago(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn,
                                            Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call buscarPagoPorId(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareCall("{call listarPagos()}");
    }

    @Override
    protected Pago mapearModelo(ResultSet rs) throws SQLException {
        return new Pago(
                leerEntero(rs, "idPago"),
                leerDecimal(rs, "monto"),
                leerFecha(rs, "fecha"),
                EstadoPago.valueOf(leerTexto(rs, "estado")),
                MetodoPago.valueOf(leerTexto(rs, "metodo"))
        );
    }
}