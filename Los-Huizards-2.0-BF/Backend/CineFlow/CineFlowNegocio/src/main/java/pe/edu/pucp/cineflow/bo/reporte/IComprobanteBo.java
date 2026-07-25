package pe.edu.pucp.cineflow.bo.reporte;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;

public interface IComprobanteBo extends IGestionable<ComprobanteCompra> {
    ComprobanteCompra obtenerPorReserva(int idReserva);
}