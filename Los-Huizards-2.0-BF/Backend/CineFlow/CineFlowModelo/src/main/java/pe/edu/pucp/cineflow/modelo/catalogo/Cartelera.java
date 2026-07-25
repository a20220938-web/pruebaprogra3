package pe.edu.pucp.cineflow.modelo.catalogo;

import java.util.ArrayList;
import java.util.List;

public class Cartelera {

    private int id;
    private List<Pelicula> peliculas;

    public Cartelera() {}

    public Cartelera(int id, List<Pelicula> peliculas) {
        this.id = id;
        this.peliculas = peliculas;
    }

    public Cartelera(Cartelera otro) {
        this.id = otro.id;
        this.peliculas = otro.peliculas;
    }

    public List<Pelicula> getPeliculas() {
        if (peliculas == null) return new ArrayList<>();
        return new ArrayList<>(peliculas);
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        if (peliculas == null) this.peliculas = new ArrayList<>();
        else this.peliculas = new ArrayList<>(peliculas);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public List<Pelicula> obtenerPeliculas() {
        if (peliculas == null) return new ArrayList<>();
        return new ArrayList<>(peliculas);
    }


    public List<Funcion> obtenerFunciones() {
        List<Funcion> funciones = new ArrayList<>();

        if (peliculas == null || peliculas.isEmpty()) {
            System.out.println("La cartelera no tiene películas registradas.");
            return funciones;
        }

        System.out.println("===== FUNCIONES EN CARTELERA (id=" + id + ") =====");
        for (Pelicula p : peliculas) {
            System.out.println("  Película: " + p.getTitulo()
                    + " [" + p.getGenero() + "] — "
                    + p.getDuracion() + " min | Clasificación: "
                    + p.getEdadRestriccion());
            // Cuando Pelicula exponga getFunciones():
            // funciones.addAll(p.getFunciones());
        }
        System.out.println("  (Consulta las funciones por película para ver horarios y salas)");
        System.out.println("=================================================");

        return funciones;
    }
}
