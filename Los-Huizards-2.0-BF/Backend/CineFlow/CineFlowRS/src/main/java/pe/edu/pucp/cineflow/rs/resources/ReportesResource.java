package pe.edu.pucp.cineflow.rs.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pe.edu.pucp.cineflow.bo.reporte.IReporteVentasBo;
import pe.edu.pucp.cineflow.bo.reporte.ReporteVentasBoImpl;
import pe.edu.pucp.cineflow.modelo.reporte.ReporteVentasPorPelicula;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Path("/v1/reportes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportesResource {

    private final IReporteVentasBo reporteBo;

    public ReportesResource() {
        this.reporteBo = new ReporteVentasBoImpl();
    }

    // RF17 - Reporte de ventas por película (con filtro opcional de cine y rango de fechas)
    @GET
    @Path("ventas/pelicula/{idPelicula}")
    public Response reporteVentasPorPelicula(
            @PathParam("idPelicula") int idPelicula,
            @QueryParam("idCine") Integer idCine,
            @QueryParam("fechaInicio") String fechaInicioStr,
            @QueryParam("fechaFin") String fechaFinStr) {

        try {
            LocalDateTime fechaInicio = null;
            LocalDateTime fechaFin = null;

            if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
                LocalDate date = LocalDate.parse(fechaInicioStr);
                fechaInicio = date.atStartOfDay();
            }
            if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
                LocalDate date = LocalDate.parse(fechaFinStr);
                fechaFin = date.atTime(LocalTime.MAX);
            }

            ReporteVentasPorPelicula reporte = reporteBo.generarPorPelicula(idPelicula, idCine, fechaInicio, fechaFin);
            return Response.ok(reporte).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
