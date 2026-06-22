package pe.edu.pucp.cineflow.bo.reporte;

import pe.edu.pucp.cineflow.modelo.reporte.ReporteVentasPorPelicula;

public interface IReporteVentasBo {
    ReporteVentasPorPelicula generarPorPelicula(int idPelicula, Integer idCine);
}
