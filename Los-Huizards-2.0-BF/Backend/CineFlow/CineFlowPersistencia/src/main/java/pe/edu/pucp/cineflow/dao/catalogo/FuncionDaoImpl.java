package pe.edu.pucp.cineflow.dao.catalogo;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.catalogo.FormatoProyeccion;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FuncionDaoImpl extends DefaultBaseDao<Funcion> implements IFuncionDao {

    @Override
    public List<Funcion> leerPorPelicula(int idPelicula) {
        return ejecutarComando(conn -> {
            try (PreparedStatement cmd = conn.prepareStatement(
                    "SELECT * FROM FUNCION WHERE id_pelicula = ?;")) {
                cmd.setInt(1, idPelicula);
                ResultSet rs = cmd.executeQuery();
                List<Funcion> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapearModelo(rs));
                return lista;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Funcion> leerPorFecha(LocalDate fecha) {
        return ejecutarComando(conn -> {
            try (PreparedStatement cmd = conn.prepareStatement(
                    "SELECT * FROM FUNCION WHERE CAST(fecha_hora AS DATE) = ?;")) {
                cmd.setDate(1, Date.valueOf(fecha));
                ResultSet rs = cmd.executeQuery();
                List<Funcion> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapearModelo(rs));
                return lista;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected PreparedStatement comandoCrear(Connection conn, Funcion modelo) throws SQLException {
        if (modelo.getPelicula() == null) throw new IllegalArgumentException("Pelicula es obligatoria");
        if (modelo.getSala() == null) throw new IllegalArgumentException("Sala es obligatoria");
        PreparedStatement cmd = conn.prepareStatement(
                "INSERT INTO FUNCION (id_pelicula, id_sala, fecha_hora, precio_base, id_formato) " +
                "VALUES (?, ?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS);
        cmd.setInt(1, modelo.getPelicula().getId());
        cmd.setInt(2, modelo.getSala().getId());
        cmd.setTimestamp(3, Timestamp.valueOf(modelo.getFechaHora()));
        cmd.setDouble(4, modelo.getPrecioBase());
        cmd.setInt(5, modelo.getFormato().ordinal() + 1);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, Funcion modelo) throws SQLException {
        if (modelo.getPelicula() == null) throw new IllegalArgumentException("Pelicula es obligatoria");
        if (modelo.getSala() == null) throw new IllegalArgumentException("Sala es obligatoria");
        PreparedStatement cmd = conn.prepareStatement(
                "UPDATE FUNCION SET id_pelicula = ?, id_sala = ?, fecha_hora = ?, " +
                "precio_base = ?, id_formato = ? WHERE id_funcion = ?;");
        cmd.setInt(1, modelo.getPelicula().getId());
        cmd.setInt(2, modelo.getSala().getId());
        cmd.setTimestamp(3, Timestamp.valueOf(modelo.getFechaHora()));
        cmd.setDouble(4, modelo.getPrecioBase());
        cmd.setInt(5, modelo.getFormato().ordinal() + 1);
        cmd.setInt(6, modelo.getId());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement("DELETE FROM FUNCION WHERE id_funcion = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "SELECT * FROM FUNCION WHERE id_funcion = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareStatement("SELECT * FROM FUNCION;");
    }

    @Override
    protected Funcion mapearModelo(ResultSet rs) throws SQLException {
        int idPelicula = leerEntero(rs, "id_pelicula");
        int idSala     = leerEntero(rs, "id_sala");
        return new Funcion(
                leerEntero(rs, "id_funcion"),
                leerFecha(rs, "fecha_hora"),
                leerDecimal(rs, "precio_base"),
                new PeliculaDaoImpl().leer(idPelicula),
                new SalaDaoImpl().leer(idSala),
                FormatoProyeccion.values()[leerEntero(rs, "id_formato") - 1]
        );
    }
}
