package pe.edu.pucp.cineflow.dao.reserva;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.dao.catalogo.FuncionDaoImpl;
import pe.edu.pucp.cineflow.dao.pago.PagoDaoImpl;
import pe.edu.pucp.cineflow.dao.usuario.UsuarioDaoImpl;
import pe.edu.pucp.cineflow.modelo.reserva.*;
import pe.edu.pucp.cineflow.modelo.pago.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ReservaDaoImpl extends DefaultBaseDao<Reserva> implements IReservaDao {


    @Override
    public Integer crear(Reserva modelo) {
        return ejecutarComando(conn -> {
            try {
                // Insertar pago si todavía no tiene id
                if (modelo.getPago() != null && modelo.getPago().getIdPago() == 0) {
                    Integer idPago = new PagoDaoImpl().crear(modelo.getPago());
                    modelo.getPago().setIdPago(idPago != null ? idPago : 0);
                }

                Integer idReserva = ejecutarComandoCrear(conn, modelo);
                if (idReserva == null || idReserva <= 0) return null;

                crearEntradas(conn, idReserva, modelo.getEntradas());
                crearAsientosReserva(conn, idReserva, modelo.getAsientos());
                crearConfiteriaReserva(conn, idReserva, modelo.getConfiteria());
                return idReserva;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    public boolean actualizar(Reserva modelo) {
        return ejecutarComando(conn -> {
            try {
                if (!ejecutarComandoActualizar(conn, modelo)) return false;

                try (PreparedStatement cmd = conn.prepareStatement(
                        "DELETE FROM entrada WHERE id_reserva = ?;")) {
                    cmd.setInt(1, modelo.getIdReserva());
                    cmd.executeUpdate();
                }
                try (PreparedStatement cmd = conn.prepareStatement(
                        "DELETE FROM reserva_asiento WHERE id_reserva = ?;")) {
                    cmd.setInt(1, modelo.getIdReserva());
                    cmd.executeUpdate();
                }

                crearEntradas(conn, modelo.getIdReserva(), modelo.getEntradas());
                crearAsientosReserva(conn, modelo.getIdReserva(), modelo.getAsientos());
                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public boolean eliminar(Integer id) {
        return ejecutarComando(conn -> {
            try {
                try (PreparedStatement cmd = conn.prepareStatement(
                        "DELETE FROM entrada WHERE id_reserva = ?;")) {
                    cmd.setInt(1, id);
                    cmd.executeUpdate();
                }
                try (PreparedStatement cmd = conn.prepareStatement(
                        "DELETE FROM reserva_asiento WHERE id_reserva = ?;")) {
                    cmd.setInt(1, id);
                    cmd.executeUpdate();
                }
                return ejecutarComandoEliminar(conn, id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    public Reserva leer(Integer id) {
        return ejecutarComando(conn -> {
            try {
                int idReserva = 0, idUsuario = 0, idPago = 0;
                String estado = null;
                Timestamp fechaReserva = null, fechaExp = null;
                double totalFinal = 0;
                boolean existe = false;

                try (CallableStatement cmd =
                             conn.prepareCall("{call buscarReservaPorId(?)}")) {
                    cmd.setInt("p_id", id);
                    ResultSet rs = cmd.executeQuery();
                    if (rs.next()) {
                        existe      = true;
                        idReserva   = leerEntero(rs, "idReserva");
                        idUsuario   = leerEnteroNullable(rs, "fid_usuario") != null
                                ? leerEnteroNullable(rs, "fid_usuario") : 0;
                        idPago      = leerEnteroNullable(rs, "fid_pago") != null
                                ? leerEnteroNullable(rs, "fid_pago") : 0;
                        estado      = leerTexto(rs, "estado");
                        totalFinal  = leerDecimal(rs, "totalFinal");
                        fechaReserva = rs.getTimestamp("fechaReserva");
                        fechaExp     = rs.getTimestamp("fechaExpiracion");
                    }
                }

                if (!existe) return null;

                Reserva r = new Reserva();
                r.setIdReserva(idReserva);
                r.setEstado(EstadoReserva.valueOf(estado));
                r.setTotalFinal(totalFinal);
                r.setFechaReserva(fechaReserva != null ? fechaReserva.toLocalDateTime() : null);
                r.setFechaExpiracion(fechaExp != null ? fechaExp.toLocalDateTime() : null);
                r.setUsuario(idUsuario > 0 ? new UsuarioDaoImpl().leer(idUsuario) : null);
                r.setPago(idPago > 0 ? new PagoDaoImpl().leer(idPago) : null);
                r.setEntradas(leerEntradas(conn, idReserva));
                r.setAsientos(leerAsientos(conn, idReserva));
                return r;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Reserva> leerTodos() {
        return ejecutarComando(conn -> {
            try {
                List<Object[]> filas = new ArrayList<>();
                try (CallableStatement cmd = conn.prepareCall("{call listarReservas()}");
                     ResultSet rs = cmd.executeQuery()) {
                    while (rs.next()) {
                        filas.add(new Object[]{
                                leerEntero(rs, "idReserva"),
                                leerEnteroNullable(rs, "fid_usuario"),
                                leerEnteroNullable(rs, "fid_pago"),
                                rs.getTimestamp("fechaReserva"),
                                rs.getTimestamp("fechaExpiracion"),
                                leerTexto(rs, "estado"),
                                leerDecimal(rs, "totalFinal")
                        });
                    }
                }

                List<Reserva> lista = new ArrayList<>(filas.size());
                for (Object[] f : filas) {
                    Reserva r = new Reserva();
                    r.setIdReserva((int) f[0]);
                    r.setFechaReserva(f[3] != null ? ((Timestamp) f[3]).toLocalDateTime() : null);
                    r.setFechaExpiracion(f[4] != null ? ((Timestamp) f[4]).toLocalDateTime() : null);
                    r.setEstado(EstadoReserva.valueOf((String) f[5]));
                    r.setTotalFinal((double) f[6]);
                    Integer uid = (Integer) f[1];
                    Integer pid = (Integer) f[2];
                    r.setUsuario(uid != null && uid > 0 ? new UsuarioDaoImpl().leer(uid) : null);
                    r.setPago(pid != null && pid > 0 ? new PagoDaoImpl().leer(pid) : null);
                    r.setEntradas(leerEntradas(conn, (int) f[0]));
                    r.setAsientos(leerAsientos(conn, (int) f[0]));
                    lista.add(r);
                }
                return lista;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    public List<Reserva> leerPorUsuario(int idUsuario) {
        return ejecutarComando(conn -> {
            try {
                List<Object[]> filas = new ArrayList<>();
                try (PreparedStatement cmd = conn.prepareStatement(
                        "SELECT * FROM RESERVA WHERE fid_usuario = ?;")) {
                    cmd.setInt(1, idUsuario);
                    ResultSet rs = cmd.executeQuery();
                    while (rs.next()) {
                        Integer pid = leerEnteroNullable(rs, "fid_pago");
                        filas.add(new Object[]{
                                leerEntero(rs, "idReserva"),
                                pid != null ? pid : 0,
                                rs.getTimestamp("fechaReserva"),
                                rs.getTimestamp("fechaExpiracion"),
                                leerTexto(rs, "estado"),
                                leerDecimal(rs, "totalFinal")
                        });
                    }
                }

                var usuario = new UsuarioDaoImpl().leer(idUsuario);
                List<Reserva> reservas = new ArrayList<>(filas.size());
                for (Object[] f : filas) {
                    Reserva r = new Reserva();
                    r.setIdReserva((int) f[0]);
                    r.setFechaReserva(f[2] != null ? ((Timestamp) f[2]).toLocalDateTime() : null);
                    r.setFechaExpiracion(f[3] != null ? ((Timestamp) f[3]).toLocalDateTime() : null);
                    r.setEstado(EstadoReserva.valueOf((String) f[4]));
                    r.setTotalFinal((double) f[5]);
                    r.setUsuario(usuario);
                    r.setPago((int) f[1] > 0 ? new PagoDaoImpl().leer((int) f[1]) : null);
                    r.setEntradas(leerEntradas(conn, (int) f[0]));
                    r.setAsientos(leerAsientos(conn, (int) f[0]));
                    reservas.add(r);
                }
                return reservas;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Reserva> leerPorFuncion(int idFuncion) {
        return ejecutarComando(conn -> {
            try {
                List<Integer> idsReserva = new ArrayList<>();
                try (PreparedStatement cmd = conn.prepareStatement(
                        "SELECT DISTINCT ra.id_reserva " +
                                "FROM reserva_asiento ra " +
                                "INNER JOIN asiento a ON ra.id_asiento = a.id_asiento " +
                                "WHERE a.id_funcion = ?;")) {
                    cmd.setInt(1, idFuncion);
                    ResultSet rs = cmd.executeQuery();
                    while (rs.next()) idsReserva.add(rs.getInt("id_reserva"));
                }

                List<Reserva> reservas = new ArrayList<>(idsReserva.size());
                for (int idReserva : idsReserva) {
                    Reserva r = leer(idReserva);
                    if (r != null) reservas.add(r);
                }
                return reservas;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    protected PreparedStatement comandoCrear(Connection conn,
                                             Reserva modelo) throws SQLException {
        CallableStatement cmd = conn.prepareCall(
                "{call insertarReserva(?, ?, ?, ?, ?, ?, ?)}");
        cmd.setTimestamp("p_fechaReserva", Timestamp.valueOf(modelo.getFechaReserva()));
        if (modelo.getFechaExpiracion() != null) {
            cmd.setTimestamp("p_fechaExpiracion", Timestamp.valueOf(modelo.getFechaExpiracion()));
        } else {
            cmd.setNull("p_fechaExpiracion", Types.TIMESTAMP);
        }
        cmd.setString("p_estado",    modelo.getEstado().name());
        cmd.setDouble("p_totalFinal", modelo.getTotalFinal());
        if (modelo.getUsuario() != null && modelo.getUsuario().getIdUsuario() > 0) {
            cmd.setInt("p_fid_usuario", modelo.getUsuario().getIdUsuario());
        } else {
            cmd.setNull("p_fid_usuario", Types.INTEGER);
        }
        if (modelo.getPago() != null && modelo.getPago().getIdPago() > 0) {
            cmd.setInt("p_fid_pago", modelo.getPago().getIdPago());
        } else {
            cmd.setNull("p_fid_pago", Types.INTEGER);
        }
        cmd.registerOutParameter("p_id", Types.INTEGER);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn,
                                                  Reserva modelo) throws SQLException {
        CallableStatement cmd = conn.prepareCall(
                "{call modificarReserva(?, ?, ?, ?, ?, ?, ?)}");
        cmd.setTimestamp("p_fechaReserva", Timestamp.valueOf(modelo.getFechaReserva()));
        if (modelo.getFechaExpiracion() != null) {
            cmd.setTimestamp("p_fechaExpiracion", Timestamp.valueOf(modelo.getFechaExpiracion()));
        } else {
            cmd.setNull("p_fechaExpiracion", Types.TIMESTAMP);
        }
        cmd.setString("p_estado",    modelo.getEstado().name());
        cmd.setDouble("p_totalFinal", modelo.getTotalFinal());
        if (modelo.getUsuario() != null && modelo.getUsuario().getIdUsuario() > 0) {
            cmd.setInt("p_fid_usuario", modelo.getUsuario().getIdUsuario());
        } else {
            cmd.setNull("p_fid_usuario", Types.INTEGER);
        }
        if (modelo.getPago() != null && modelo.getPago().getIdPago() > 0) {
            cmd.setInt("p_fid_pago", modelo.getPago().getIdPago());
        } else {
            cmd.setNull("p_fid_pago", Types.INTEGER);
        }
        cmd.setInt("p_id", modelo.getIdReserva());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn,
                                                Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call eliminarReserva(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn,
                                            Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call buscarReservaPorId(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareCall("{call listarReservas()}");
    }

    // Mapeo ligero sin sub-entidades (uso interno de BaseDao)
    @Override
    protected Reserva mapearModelo(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setIdReserva(leerEntero(rs, "idReserva"));
        r.setFechaReserva(leerFecha(rs, "fechaReserva"));
        r.setFechaExpiracion(leerFecha(rs, "fechaExpiracion"));
        r.setEstado(EstadoReserva.valueOf(leerTexto(rs, "estado")));
        r.setTotalFinal(leerDecimal(rs, "totalFinal"));
        return r;
    }


    private void crearEntradas(Connection conn, int idReserva,
                               List<Entrada> entradas) throws SQLException {
        if (entradas == null) return;
        for (Entrada entrada : entradas) {
            try (PreparedStatement cmd = conn.prepareStatement(
                    "INSERT INTO entrada (tipo, precio_base, id_reserva) VALUES (?, ?, ?);")) {
                cmd.setString(1, entrada.getTipo().name());
                cmd.setDouble(2, entrada.getPrecioBase());
                cmd.setInt(3, idReserva);
                cmd.executeUpdate();
            }
        }
    }

    private List<Entrada> leerEntradas(Connection conn,
                                       int idReserva) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        try (PreparedStatement cmd = conn.prepareStatement(
                "SELECT * FROM entrada WHERE id_reserva = ?;")) {
            cmd.setInt(1, idReserva);
            ResultSet rs = cmd.executeQuery();
            while (rs.next()) {
                filas.add(new Object[]{
                        leerEntero(rs, "id_entrada"),
                        leerDecimal(rs, "precio_base"),
                        leerTexto(rs, "tipo")
                });
            }
        }

        List<Entrada> entradas = new ArrayList<>(filas.size());
        for (Object[] f : filas) {
            entradas.add(new Entrada((int) f[0], (double) f[1],
                    tipoEntrada.valueOf((String) f[2])));
        }
        return entradas;
    }


    private void crearAsientosReserva(Connection conn, int idReserva,
                                      List<Asiento> asientos) throws SQLException {
        if (asientos == null) return;
        for (Asiento asiento : asientos) {
            try (PreparedStatement cmd = conn.prepareStatement(
                    "INSERT INTO reserva_asiento (id_reserva, id_asiento) VALUES (?, ?);")) {
                cmd.setInt(1, idReserva);
                cmd.setInt(2, asiento.getIdAsiento());
                cmd.executeUpdate();
            }
            try (PreparedStatement cmd = conn.prepareStatement(
                    "UPDATE asiento SET id_estado_asiento = 3 WHERE id_asiento = ?;")) {
                cmd.setInt(1, asiento.getIdAsiento());
                cmd.executeUpdate();
            }
        }
    }
    private void crearConfiteriaReserva(Connection conn, int idReserva,
                                        Confiteria confiteria) throws SQLException {
        if (confiteria == null) return;
        try (PreparedStatement cmd = conn.prepareStatement(
                "INSERT INTO RESERVA_CONFITERIA (id_reserva, id_item, cantidad, precio_unitario) " +
                        "VALUES (?, ?, ?, ?);")) {
            cmd.setInt(1, idReserva);
            cmd.setInt(2, confiteria.getIdItem());
            cmd.setInt(3, confiteria.getCantidad());
            cmd.setDouble(4, confiteria.getPrecioUnitario());
            cmd.executeUpdate();
        }
    }
    private List<Asiento> leerAsientos(Connection conn,
                                       int idReserva) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        try (PreparedStatement cmd = conn.prepareStatement(
                "SELECT a.id_asiento, a.fila, a.numero, a.id_estado_asiento, a.id_funcion " +
                        "FROM asiento a " +
                        "INNER JOIN reserva_asiento ra ON a.id_asiento = ra.id_asiento " +
                        "WHERE ra.id_reserva = ?;")) {
            cmd.setInt(1, idReserva);
            ResultSet rs = cmd.executeQuery();
            while (rs.next()) {
                filas.add(new Object[]{
                        leerEntero(rs, "id_asiento"),
                        leerTexto(rs, "fila").charAt(0),
                        leerEntero(rs, "numero"),
                        leerEntero(rs, "id_estado_asiento"),
                        leerEntero(rs, "id_funcion")
                });
            }
        }

        List<Asiento> asientos = new ArrayList<>(filas.size());
        for (Object[] f : filas) {
            var funcion = new FuncionDaoImpl().leer((int) f[4]);
            EstadoAsiento estado = EstadoAsiento.values()[(int) f[3] - 1];
            asientos.add(new Asiento((int) f[0], (char) f[1], (int) f[2], false, estado, funcion));
        }
        return asientos;
    }
}