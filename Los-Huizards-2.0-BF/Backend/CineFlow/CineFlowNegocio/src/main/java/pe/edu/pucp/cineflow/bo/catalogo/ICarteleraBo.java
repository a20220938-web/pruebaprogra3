package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.catalogo.Cartelera;
import pe.edu.pucp.cineflow.modelo.catalogo.FiltroBusqueda;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;

import java.util.List;

public interface ICarteleraBo extends IGestionable<Cartelera> {
    Cartelera obtenerPorCine(int idCine);
    List<Pelicula> buscarPeliculas(FiltroBusqueda filtro);
}
