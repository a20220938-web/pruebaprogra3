package pe.edu.pucp.cineflow.dao;

import pe.edu.pucp.cineflow.db.DBFactoryProvider;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public abstract class BaseDao<M, I> implements IPersistible<M, I> {



    @Override
    public I crear(M modelo) {
        return ejecutarComando(conn -> ejecutarComandoCrear(conn, modelo));
    }

    @Override
    public boolean actualizar(M modelo) {
        return ejecutarComando(conn -> ejecutarComandoActualizar(conn, modelo));
    }

    @Override
    public boolean eliminar(I id) {
        return ejecutarComando(conn -> ejecutarComandoEliminar(conn, id));
    }

    @Override
    public M leer(I id) {
        return ejecutarComando(conn -> {
            try (PreparedStatement cmd = comandoLeer(conn, id)) {
                ResultSet rs = cmd.executeQuery();
                return rs.next() ? mapearModelo(rs) : null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<M> leerTodos() {
        return ejecutarComando(conn -> {
            try (PreparedStatement cmd = comandoLeerTodos(conn);
                 ResultSet rs = cmd.executeQuery()) {
                List<M> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapearModelo(rs));
                return lista;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    protected <T> T ejecutarComando(Function<Connection, T> comando) {
        Connection txConn = TransactionsManager.obtenerConexionActual();
        if (txConn != null) {
            return comando.apply(txConn);
        }
        try (Connection conn = DBFactoryProvider.getManager().getConnection()) {
            return comando.apply(conn);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al obtener conexion", e);
        }
    }


    protected I ejecutarComandoCrear(Connection conn, M modelo) {
        try (PreparedStatement cmd = comandoCrear(conn, modelo)) {
            if (cmd instanceof CallableStatement) {
                cmd.execute();              // ← stored procedure con OUT
            } else {
                if (cmd.executeUpdate() == 0) return null;
            }
            return extraerIdDespuesDeCrear(cmd, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean ejecutarComandoActualizar(Connection conn, M modelo) {
        try (PreparedStatement cmd = comandoActualizar(conn, modelo)) {
            return cmd.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean ejecutarComandoEliminar(Connection conn, I id) {
        try (PreparedStatement cmd = comandoEliminar(conn, id)) {
            return cmd.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    protected abstract PreparedStatement comandoCrear(Connection conn, M modelo) throws SQLException;
    protected abstract PreparedStatement comandoActualizar(Connection conn, M modelo) throws SQLException;
    protected abstract PreparedStatement comandoEliminar(Connection conn, I id) throws SQLException;
    protected abstract PreparedStatement comandoLeer(Connection conn, I id) throws SQLException;
    protected abstract PreparedStatement comandoLeerTodos(Connection conn) throws SQLException;
    protected abstract M mapearModelo(ResultSet rs) throws SQLException;
    protected abstract I extraerIdDespuesDeCrear(PreparedStatement cmd, Connection conn) throws SQLException;


    protected int leerEntero(ResultSet rs, String columna) throws SQLException {
        return rs.getInt(columna);
    }

    protected Integer leerEnteroNullable(ResultSet rs, String columna) throws SQLException {
        int value = rs.getInt(columna);
        return rs.wasNull() ? null : value;
    }

    protected String leerTexto(ResultSet rs, String columna) throws SQLException {
        String value = rs.getString(columna);
        if (value == null) {
            throw new IllegalStateException("La columna " + columna + " no contiene texto válido");
        }
        return value;
    }

    protected double leerDecimal(ResultSet rs, String columna) throws SQLException {
        double value = rs.getDouble(columna);
        return rs.wasNull() ? 0.0 : value;
    }

    protected boolean leerBooleano(ResultSet rs, String columna) throws SQLException {
        return rs.getBoolean(columna);
    }

    protected java.time.LocalDateTime leerFecha(ResultSet rs, String columna) throws SQLException {
        Timestamp ts = rs.getTimestamp(columna);
        return ts != null ? ts.toLocalDateTime() : null;
    }

    protected java.time.LocalDate leerFechaSolo(ResultSet rs, String columna) throws SQLException {
        Date d = rs.getDate(columna);
        return d != null ? d.toLocalDate() : null;
    }
}
