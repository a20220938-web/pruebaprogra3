package pe.edu.pucp.cineflow.dao.catalogo;

import pe.edu.pucp.cineflow.dao.IPersistible;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;

import java.time.LocalDate;
import java.util.List;

public interface IFuncionDao extends IPersistible<Funcion, Integer> {
    List<Funcion> leerPorPelicula(int idPelicula);
    List<Funcion> leerPorFecha(LocalDate fecha);
}
