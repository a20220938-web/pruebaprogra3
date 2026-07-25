package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;

import java.time.LocalDateTime;
import java.util.List;

public interface IFuncionBo extends IGestionable<Funcion> {
    List<Funcion> listarPorPelicula(int idPelicula);
    List<Funcion> listarPorFecha(LocalDateTime fecha);
}