package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.catalogo.Sala;

import java.util.List;

public interface ISalaBo extends IGestionable<Sala> {
    List<Sala> listarPorCine(int idCine);
}