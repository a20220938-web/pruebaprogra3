package pe.edu.pucp.cineflow.dao.reporte;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.dao.reserva.ReservaDaoImpl;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;

import java.sql.*;


public class ComprobanteCompraDaoImpl extends DefaultBaseDao<ComprobanteCompra>
        implements IComprobanteCompraDao {


    @Override
    public ComprobanteCompra leerPorReserva(int idReserva) {
        return ejecutarComando(conn -> {
            try {
                int idComprobante = 0;
                String codigoQr = null;
                double montoTotal = 0;
                boolean existe = false;

                try (CallableStatement cmd =
                             conn.prepareCall("{call buscarComprobantePorReserva(?)}")) {
                    cmd.setInt("p_id_reserva", idReserva);
                    ResultSet rs = cmd.executeQuery();
                    if (rs.next()) {
                        existe         = true;
                        idComprobante  = leerEntero(rs, "id_comprobante");
                        codigoQr       = rs.getString("codigo_qr");
                        montoTotal     = leerDecimal(rs, "monto_total");
                    }
                }

                if (!existe) return null;

                // Cursor cerrado — ahora cargamos la reserva
                Reserva reserva = new ReservaDaoImpl().leer(idReserva);
                return new ComprobanteCompra(idComprobante,
                        codigoQr != null ? codigoQr : "", montoTotal, reserva);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Sobreescribimos leer() porque necesita hidratación de Reserva
    @Override
    public ComprobanteCompra leer(Integer id) {
        return ejecutarComando(conn -> {
            try {
                int idComprobante = 0, idReserva = 0;
                String codigoQr = null;
                double montoTotal = 0;
                boolean existe = false;

                try (CallableStatement cmd =
                             conn.prepareCall("{call buscarComprobantePorId(?)}")) {
                    cmd.setInt("p_id", id);
                    ResultSet rs = cmd.executeQuery();
                    if (rs.next()) {
                        existe        = true;
                        idComprobante = leerEntero(rs, "id_comprobante");
                        idReserva     = leerEntero(rs, "id_reserva");
                        codigoQr      = rs.getString("codigo_qr");
                        montoTotal    = leerDecimal(rs, "monto_total");
                    }
                }

                if (!existe) return null;

                Reserva reserva = new ReservaDaoImpl().leer(idReserva);
                return new ComprobanteCompra(idComprobante,
                        codigoQr != null ? codigoQr : "", montoTotal, reserva);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected PreparedStatement comandoCrear(Connection conn,
                                             ComprobanteCompra modelo) throws SQLException {
        if (modelo.getReserva() == null) {
            throw new IllegalArgumentException("La reserva es obligatoria para el comprobante");
        }
        CallableStatement cmd = conn.prepareCall(
                "{call insertarComprobante(?, ?, ?, ?)}");
        String qr = modelo.getCodigoQR();
        if (qr == null || qr.isEmpty()) {
            cmd.setNull("p_codigo_qr", Types.VARCHAR);
        } else {
            cmd.setString("p_codigo_qr", qr);
        }
        cmd.setDouble("p_monto_total", modelo.getMontoTotal());
        cmd.setInt("p_id_reserva",     modelo.getReserva().getIdReserva());
        cmd.registerOutParameter("p_id", Types.INTEGER);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn,
                                                  ComprobanteCompra modelo) throws SQLException {
        throw new UnsupportedOperationException(
                "Un comprobante no puede modificarse una vez emitido.");
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn,
                                                Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call eliminarComprobante(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn,
                                            Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call buscarComprobantePorId(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareCall("{call listarComprobantes()}");
    }

    @Override
    protected ComprobanteCompra mapearModelo(ResultSet rs) throws SQLException {
        Reserva reservaShell = new Reserva();
        reservaShell.setIdReserva(leerEntero(rs, "id_reserva"));
        return new ComprobanteCompra(
                leerEntero(rs, "id_comprobante"),
                rs.getString("codigo_qr") != null ? rs.getString("codigo_qr") : "",
                leerDecimal(rs, "monto_total"),
                reservaShell
        );
    }
}
