package pe.edu.pucp.cineflow.dao.reporte;

import pe.edu.pucp.cineflow.dao.IPersistible;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;

public interface IComprobanteCompraDao extends IPersistible<ComprobanteCompra, Integer> {
    ComprobanteCompra leerPorReserva(int idReserva);
}
