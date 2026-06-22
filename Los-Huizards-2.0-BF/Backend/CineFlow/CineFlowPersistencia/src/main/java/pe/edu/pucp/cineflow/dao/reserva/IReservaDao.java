package pe.edu.pucp.cineflow.dao.reserva;

import pe.edu.pucp.cineflow.dao.IPersistible;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;

import java.util.List;

public interface IReservaDao extends IPersistible<Reserva, Integer> {
    List<Reserva> leerPorUsuario(int idUsuario);
    List<Reserva> leerPorFuncion(int idFuncion);
}
