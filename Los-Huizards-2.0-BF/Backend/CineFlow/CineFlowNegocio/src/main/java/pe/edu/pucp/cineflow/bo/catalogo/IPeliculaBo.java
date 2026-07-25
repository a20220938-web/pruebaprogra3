package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.catalogo.Genero;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;

import java.time.LocalDate;
import java.util.List;

public interface IPeliculaBo extends IGestionable<Pelicula> {
    List<Pelicula> listarPorGenero(Genero genero);
    List<Pelicula> buscar(String nombre, String cine, LocalDate fecha, Genero genero);
}