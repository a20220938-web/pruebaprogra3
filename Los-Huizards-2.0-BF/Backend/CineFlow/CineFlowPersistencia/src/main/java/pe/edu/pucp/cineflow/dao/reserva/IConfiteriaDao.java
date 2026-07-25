package pe.edu.pucp.cineflow.dao.reserva;

import pe.edu.pucp.cineflow.dao.IPersistible;
import pe.edu.pucp.cineflow.modelo.reserva.ArticuloIndividual;

import java.util.List;

public interface IConfiteriaDao extends IPersistible<ArticuloIndividual, Integer> {
    List<ArticuloIndividual> leerPorInventario(int idInventario);
}
