package pe.edu.pucp.cineflow.dao.reserva;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;
import pe.edu.pucp.cineflow.dao.catalogo.FuncionDaoImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsientoDaoImpl extends DefaultBaseDao<Asiento> implements IAsientoDao {

    @Override
    public List<Asiento> leerPorFuncion(int idFuncion) {
        return ejecutarComando(conn -> {
            try {
                List<Object[]> filas = new ArrayList<>();
                try (PreparedStatement cmd = conn.prepareStatement(
                        "SELECT * FROM asiento WHERE id_funcion = ?;")) {
                    cmd.setInt(1, idFuncion);
                    ResultSet rs = cmd.executeQuery();
                    while (rs.next()) {
                        filas.add(new Object[]{
                                leerEntero(rs, "id_asiento"),
                                leerTexto(rs, "fila").charAt(0),
                                leerEntero(rs, "numero"),
                                leerEntero(rs, "id_estado_asiento")
                        });
                    }
                }
                // Reader cerrado — ahora sí podemos llamar a FuncionDaoImpl sin recursión
                Funcion funcion = new FuncionDaoImpl().leer(idFuncion);
                List<Asiento> lista = new ArrayList<>(filas.size());
                for (Object[] f : filas) {
                    EstadoAsiento estado = EstadoAsiento.values()[(int) f[3] - 1];
                    char fila = (char) f[1];
                    int numero = (int) f[2];
                    boolean esConadis = (fila == 'A' && numero <= 2);
                    lista.add(new Asiento((int) f[0], fila, numero, esConadis, estado, funcion));
                }
                return lista;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected PreparedStatement comandoCrear(Connection conn, Asiento modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "INSERT INTO asiento (fila, numero, id_estado_asiento, id_funcion) VALUES (?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS);
        cmd.setString(1, String.valueOf(modelo.getFila()));
        cmd.setInt(2, modelo.getNumero());
        cmd.setInt(3, modelo.getEstado().ordinal() + 1);
        cmd.setInt(4, modelo.getFuncion().getId());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, Asiento modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "UPDATE asiento SET fila = ?, numero = ?, id_estado_asiento = ? WHERE id_asiento = ?;");
        cmd.setString(1, String.valueOf(modelo.getFila()));
        cmd.setInt(2, modelo.getNumero());
        cmd.setInt(3, modelo.getEstado().ordinal() + 1);
        cmd.setInt(4, modelo.getIdAsiento());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement("DELETE FROM asiento WHERE id_asiento = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement("SELECT * FROM asiento WHERE id_asiento = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareStatement("SELECT * FROM asiento;");
    }

    @Override
    protected Asiento mapearModelo(ResultSet rs) throws SQLException {
        EstadoAsiento estado = EstadoAsiento.values()[leerEntero(rs, "id_estado_asiento") - 1];
        char fila = leerTexto(rs, "fila").charAt(0);
        int numero = leerEntero(rs, "numero");
        boolean esConadis = (fila == 'A' && numero <= 2);
        
        return new Asiento(
                leerEntero(rs, "id_asiento"),
                fila,
                numero,
                esConadis,
                estado,
                null
        );
    }
}