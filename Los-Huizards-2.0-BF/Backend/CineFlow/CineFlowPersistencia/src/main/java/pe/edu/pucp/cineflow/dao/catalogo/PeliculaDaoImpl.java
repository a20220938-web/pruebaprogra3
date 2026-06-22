package pe.edu.pucp.cineflow.dao.catalogo;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.catalogo.Genero;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;

import java.sql.*;


public class PeliculaDaoImpl extends DefaultBaseDao<Pelicula> implements IPeliculaDao {

    @Override
    protected PreparedStatement comandoCrear(Connection conn,
                                             Pelicula modelo) throws SQLException {
        CallableStatement cmd = conn.prepareCall(
                "{call insertarPelicula(?, ?, ?, ?, ?, ?)}");
        cmd.setString("p_titulo",           modelo.getTitulo());
        cmd.setInt("p_duracion",            modelo.getDuracion());
        cmd.setString("p_sinopsis",         modelo.getSinopsis());
        cmd.setString("p_edad_restriccion", modelo.getEdadRestriccion());
        cmd.setInt("p_genero",              modelo.getGenero().ordinal());
        cmd.registerOutParameter("p_id", Types.INTEGER);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn,
                                                  Pelicula modelo) throws SQLException {
        CallableStatement cmd = conn.prepareCall(
                "{call modificarPelicula(?, ?, ?, ?, ?, ?)}");
        cmd.setString("p_titulo",           modelo.getTitulo());
        cmd.setInt("p_duracion",            modelo.getDuracion());
        cmd.setString("p_sinopsis",         modelo.getSinopsis());
        cmd.setString("p_edad_restriccion", modelo.getEdadRestriccion());
        cmd.setInt("p_genero",              modelo.getGenero().ordinal());
        cmd.setInt("p_id",                  modelo.getId());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn,
                                                Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call eliminarPelicula(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn,
                                            Integer id) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call buscarPeliculaPorId(?)}");
        cmd.setInt("p_id", id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareCall("{call listarPeliculas()}");
    }

    @Override
    protected Pelicula mapearModelo(ResultSet rs) throws SQLException {
        return new Pelicula(
                leerEntero(rs, "id"),
                leerTexto(rs, "titulo"),
                leerEntero(rs, "duracion"),
                leerTexto(rs, "sinopsis"),
                Genero.values()[leerEntero(rs, "genero")],
                leerTexto(rs, "edad_restriccion")
        );
    }
}