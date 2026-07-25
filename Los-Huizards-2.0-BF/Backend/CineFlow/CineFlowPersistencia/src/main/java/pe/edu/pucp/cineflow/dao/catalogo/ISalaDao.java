package pe.edu.pucp.cineflow.dao.catalogo;

import pe.edu.pucp.cineflow.dao.IPersistible;
import pe.edu.pucp.cineflow.modelo.catalogo.Sala;

import java.util.List;

public interface ISalaDao extends IPersistible<Sala, Integer> {
    List<Sala> listarPorCine(int idCine);
}
