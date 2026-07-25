package pe.edu.pucp.cineflow.dao.catalogo;

import pe.edu.pucp.cineflow.dao.DefaultBaseDao;
import pe.edu.pucp.cineflow.modelo.catalogo.Cartelera;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarteleraDaoImpl extends DefaultBaseDao<Cartelera> implements ICarteleraDao {

    @Override
    public Cartelera leerPorCine(int idCine) {
        return ejecutarComando(conn -> {
            try {
                int idCartelera = 0;
                boolean existe = false;

                try (PreparedStatement cmd = conn.prepareStatement(
                        "SELECT * FROM CARTELERA WHERE id_cine = ? LIMIT 1;")) {
                    cmd.setInt(1, idCine);
                    ResultSet rs = cmd.executeQuery();
                    if (rs.next()) {
                        existe = true;
                        idCartelera = leerEntero(rs, "id_cartelera");
                    }
                }

                if (!existe) return null;

                List<Pelicula> peliculas = leerPeliculasDeCartelera(conn, idCartelera);
                return new Cartelera(idCartelera, peliculas);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Integer crear(Cartelera modelo) {
        return ejecutarComando(conn -> {
            Integer idCartelera = ejecutarComandoCrear(conn, modelo);
            if (idCartelera == null || idCartelera <= 0) return null;
            asociarPeliculas(conn, idCartelera, modelo.getPeliculas());
            return idCartelera;
        });
    }

    @Override
    public boolean actualizar(Cartelera modelo) {
        return ejecutarComando(conn -> {
            try {
                if (!ejecutarComandoActualizar(conn, modelo)) return false;

                try (PreparedStatement cmd = conn.prepareStatement(
                        "DELETE FROM CARTELERA_PELICULA WHERE id_cartelera = ?;")) {
                    cmd.setInt(1, modelo.getId());
                    cmd.executeUpdate();
                }

                asociarPeliculas(conn, modelo.getId(), modelo.getPeliculas());
                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Cartelera leer(Integer id) {
        return ejecutarComando(conn -> {
            try {
                int idCartelera = 0;
                boolean existe = false;

                try (PreparedStatement cmd = comandoLeer(conn, id)) {
                    ResultSet rs = cmd.executeQuery();
                    if (rs.next()) {
                        existe = true;
                        idCartelera = leerEntero(rs, "id_cartelera");
                    }
                }

                if (!existe) return null;

                List<Pelicula> peliculas = leerPeliculasDeCartelera(conn, idCartelera);
                return new Cartelera(idCartelera, peliculas);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Cartelera> leerTodos() {
        return ejecutarComando(conn -> {
            try {
                List<Integer> ids = new ArrayList<>();

                try (PreparedStatement cmd = comandoLeerTodos(conn);
                     ResultSet rs = cmd.executeQuery()) {
                    while (rs.next()) ids.add(leerEntero(rs, "id_cartelera"));
                }

                List<Cartelera> carteleras = new ArrayList<>(ids.size());
                for (int id : ids) {
                    List<Pelicula> peliculas = leerPeliculasDeCartelera(conn, id);
                    carteleras.add(new Cartelera(id, peliculas));
                }
                return carteleras;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // -----------------------------------------------------------------------
    // Comandos SQL — tabla CARTELERA
    // -----------------------------------------------------------------------

    @Override
    protected PreparedStatement comandoCrear(Connection conn, Cartelera modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "INSERT INTO CARTELERA (descripcion, id_cine) VALUES (?, ?);",
                Statement.RETURN_GENERATED_KEYS);
        cmd.setNull(1, Types.VARCHAR);
        cmd.setInt(2, 0);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoActualizar(Connection conn, Cartelera modelo) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "UPDATE CARTELERA SET descripcion = ? WHERE id_cartelera = ?;");
        cmd.setNull(1, Types.VARCHAR);
        cmd.setInt(2, modelo.getId());
        return cmd;
    }

    @Override
    protected PreparedStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "DELETE FROM CARTELERA WHERE id_cartelera = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeer(Connection conn, Integer id) throws SQLException {
        PreparedStatement cmd = conn.prepareStatement(
                "SELECT * FROM CARTELERA WHERE id_cartelera = ?;");
        cmd.setInt(1, id);
        return cmd;
    }

    @Override
    protected PreparedStatement comandoLeerTodos(Connection conn) throws SQLException {
        return conn.prepareStatement("SELECT * FROM CARTELERA;");
    }

    @Override
    protected Cartelera mapearModelo(ResultSet rs) throws SQLException {
        return new Cartelera(leerEntero(rs, "id_cartelera"), new ArrayList<>());
    }


    private void asociarPeliculas(Connection conn, int idCartelera, List<Pelicula> peliculas) {
        if (peliculas == null) return;
        try {
            for (Pelicula pelicula : peliculas) {
                try (PreparedStatement cmd = conn.prepareStatement(
                        "INSERT INTO CARTELERA_PELICULA (id_cartelera, id_pelicula) VALUES (?, ?);")) {
                    cmd.setInt(1, idCartelera);
                    cmd.setInt(2, pelicula.getId());
                    cmd.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Pelicula> leerPeliculasDeCartelera(Connection conn, int idCartelera) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement cmd = conn.prepareStatement(
                "SELECT id_pelicula FROM CARTELERA_PELICULA WHERE id_cartelera = ?;")) {
            cmd.setInt(1, idCartelera);
            ResultSet rs = cmd.executeQuery();
            while (rs.next()) ids.add(rs.getInt("id_pelicula"));
        }

        // Reader cerrado — ahora llamamos a PeliculaDaoImpl
        List<Pelicula> peliculas = new ArrayList<>(ids.size());
        for (int idPelicula : ids) {
            Pelicula p = new PeliculaDaoImpl().leer(idPelicula);
            if (p != null) peliculas.add(p);
        }
        return peliculas;
    }
}
