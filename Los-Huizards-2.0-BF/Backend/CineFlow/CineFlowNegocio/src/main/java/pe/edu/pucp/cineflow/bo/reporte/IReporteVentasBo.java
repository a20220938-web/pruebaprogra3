package pe.edu.pucp.cineflow.bo.reporte;

import pe.edu.pucp.cineflow.modelo.reporte.ReporteVentasPorPelicula;

import java.time.LocalDateTime;

public interface IReporteVentasBo {
    ReporteVentasPorPelicula generarPorPelicula(int idPelicula, Integer idCine, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
