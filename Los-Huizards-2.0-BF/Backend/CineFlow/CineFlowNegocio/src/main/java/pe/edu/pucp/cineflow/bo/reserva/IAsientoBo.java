package pe.edu.pucp.cineflow.bo.reserva;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;

import java.util.List;

public interface IAsientoBo extends IGestionable<Asiento> {
    List<Asiento> listarPorFuncion(int idFuncion);
    void cambiarEstado(int idAsiento, EstadoAsiento nuevoEstado);
}
