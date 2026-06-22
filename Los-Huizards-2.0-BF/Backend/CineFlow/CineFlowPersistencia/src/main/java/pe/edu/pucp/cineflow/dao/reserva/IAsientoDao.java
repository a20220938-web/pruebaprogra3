package pe.edu.pucp.cineflow.dao.reserva;

import pe.edu.pucp.cineflow.dao.IPersistible;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import java.util.List;

public interface IAsientoDao extends IPersistible<Asiento, Integer> {
    List<Asiento> leerPorFuncion(int idFuncion);
}