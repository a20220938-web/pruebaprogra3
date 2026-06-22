package pe.edu.pucp.cineflow.bo.reserva;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.reserva.ArticuloIndividual;

import java.util.List;

public interface IConfiteriaBo extends IGestionable<ArticuloIndividual> {

    List<ArticuloIndividual> listarPorInventario(int idInventario);
}
