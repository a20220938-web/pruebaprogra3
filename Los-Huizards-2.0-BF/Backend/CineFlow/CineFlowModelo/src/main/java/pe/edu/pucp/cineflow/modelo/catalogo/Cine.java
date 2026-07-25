package pe.edu.pucp.cineflow.modelo.catalogo;

import java.util.ArrayList;
import java.util.List;

public class Cine {

    private int id;
    private String nombre;
    private String ubicacion;
    private Cartelera cartelera;

    public Cine() {}

    public Cine(int id, String nombre, String ubicacion, Cartelera cartelera) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.cartelera = cartelera;
    }

    public Cine(Cine otro) {
        this.id = otro.id;
        this.nombre = otro.nombre;
        this.ubicacion = otro.ubicacion;
        this.cartelera = otro.cartelera;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    @jakarta.json.bind.annotation.JsonbTransient
    public Cartelera getCartelera() { return cartelera; }
    public void setCartelera(Cartelera cartelera) { this.cartelera = cartelera; }


    public List<Pelicula> listarPeliculas() {
        if (cartelera == null) {
            System.out.println("El cine \"" + nombre + "\" no tiene cartelera asignada.");
            return new ArrayList<>();
        }
        List<Pelicula> peliculas = cartelera.obtenerPeliculas();
        System.out.println("===== PELÍCULAS EN CARTELERA — " + nombre + " =====");
        if (peliculas.isEmpty()) {
            System.out.println("  No hay películas en cartelera.");
        } else {
            for (Pelicula p : peliculas) {
                System.out.println("  [" + p.getId() + "] " + p.getTitulo()
                        + " | " + p.getGenero()
                        + " | " + p.getDuracion() + " min"
                        + " | " + p.getEdadRestriccion());
            }
        }
        System.out.println("==============================================");
        return peliculas;
    }


    public List<Funcion> listarFunciones() {
        if (cartelera == null) {
            System.out.println("El cine \"" + nombre + "\" no tiene cartelera asignada.");
            return new ArrayList<>();
        }
        return cartelera.obtenerFunciones();
    }
}
