package pe.edu.pucp.cineflow.reportes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperReport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ReporteVentasPelicula", urlPatterns = {"/reportes/ventas-pelicula"})
public class ReporteVentasPelicula extends JasperReporteBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            JasperReport reporte = obtenerReporte("ventas_pelicula");
            Map<String, Object> params = new HashMap<>();
            params.put("idCine", parseIntOrNull(request.getParameter("idCine")));
            params.put("desde", emptyToNull(request.getParameter("desde")));
            params.put("hasta", emptyToNull(request.getParameter("hasta")));
            generarPdf(response, reporte, params);
        } catch (Exception ex) {
            escribirError(response, ex);
        }
    }

    private Integer parseIntOrNull(String v) {
        try { return (v == null || v.isBlank()) ? null : Integer.valueOf(v.trim()); }
        catch (NumberFormatException e) { return null; }
    }
    private String emptyToNull(String v) { return (v == null || v.isBlank()) ? null : v.trim(); }
}
