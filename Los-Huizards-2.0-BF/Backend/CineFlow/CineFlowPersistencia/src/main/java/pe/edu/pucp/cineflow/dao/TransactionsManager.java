package pe.edu.pucp.cineflow.dao;

import pe.edu.pucp.cineflow.db.DBFactoryProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Savepoint;


public class TransactionsManager {

    private static final ThreadLocal<Connection> conexionActual = new ThreadLocal<>();

    private TransactionsManager() { }

    public static void iniciarTransaccion() {
        if (hayTransaccionActiva()) {
            throw new IllegalStateException("Ya existe una transaccion activa en este hilo");
        }
        try {
            Connection conn = DBFactoryProvider.getManager().getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conexionActual.set(conn);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("No se pudo iniciar la transaccion", e);
        }
    }

    public static void commitTransaccion() {
        Connection conn = obtenerConexionActiva();
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error al hacer commit de la transaccion", e);
        } finally {
            cerrarRecursosActuales();
        }
    }

    public static void rollbackTransaccion() {
        Connection conn = obtenerConexionActiva();
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new RuntimeException("Error al hacer rollback de la transaccion", e);
        } finally {
            cerrarRecursosActuales();
        }
    }

    /** Devuelve la conexión activa del hilo actual, o null si no hay transacción. */
    public static Connection obtenerConexionActual() {
        return conexionActual.get();
    }

    public static boolean hayTransaccionActiva() {
        return conexionActual.get() != null;
    }



    private static Connection obtenerConexionActiva() {
        Connection conn = conexionActual.get();
        if (conn == null) {
            throw new IllegalStateException("No hay una transaccion activa en este hilo");
        }
        return conn;
    }

    private static void cerrarRecursosActuales() {
        Connection conn = conexionActual.get();
        try {
            if (conn != null) {
                conn.setAutoCommit(true); // restaurar estado por defecto
                conn.close();
            }
        } catch (SQLException ignored) {
        } finally {
            conexionActual.remove();
        }
    }
}
