package pe.edu.pucp.cineflow.dao.catalogo;

import pe.edu.pucp.cineflow.dao.IPersistible;
import pe.edu.pucp.cineflow.modelo.catalogo.Cartelera;

public interface ICarteleraDao extends IPersistible<Cartelera, Integer> {
    Cartelera leerPorCine(int idCine);
}
