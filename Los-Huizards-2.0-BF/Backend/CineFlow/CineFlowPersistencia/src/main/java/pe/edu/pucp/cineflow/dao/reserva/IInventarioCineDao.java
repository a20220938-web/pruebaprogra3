package pe.edu.pucp.cineflow.dao.reserva;

import pe.edu.pucp.cineflow.dao.IPersistible;
import pe.edu.pucp.cineflow.modelo.reserva.InventarioCine;

public interface IInventarioCineDao extends IPersistible<InventarioCine, Integer> {
    InventarioCine leerPorCine(int idCine);
    void recalcularStock();
}