package pe.edu.pucp.cineflow.bo.reserva;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.reserva.InventarioCine;

public interface IInventarioCineBo extends IGestionable<InventarioCine> {

    InventarioCine obtenerPorCine(int idCine);


    boolean verificarDisponibilidad(int idInventario, int cantidadRequerida);
}