package pe.edu.pucp.cineflow.reportes;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperReport;
import pe.edu.pucp.cineflow.db.DBFactoryProvider;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprobante de reserva (RF16) estilo Cinemark: logo, QR, secciones con
 * ENTRADAS / ASIENTOS / CONFITERIA y resumen de pago.
 * URL: GET /reportes/comprobante?id={idReserva}
 */
@WebServlet(name = "ReporteComprobanteReserva", urlPatterns = {"/reportes/comprobante"})
public class ReporteComprobanteReserva extends JasperReporteBase {
    private static final String LOGO = "reportes/cineflow-logo.png";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parametro 'id'");
                return;
            }
            int idReserva = Integer.parseInt(idParam.trim());

            JasperReport maestro = obtenerReporte("comprobante_reserva");
            JasperReport detalle = obtenerReporte("comprobante_reserva_detalle");

            String codigo = obtenerCodigo(idReserva);   // codigo_qr del comprobante, o RES-id

            Map<String, Object> params = new HashMap<>();
            params.put("id_reserva", idReserva);
            params.put("subreporte", detalle);
            params.put("logo", cargarLogo());
            params.put("codigo", codigo);
            params.put("qr", generarQr(codigo, 220));   // QR a partir del mismo codigo

            generarPdf(response, maestro, params);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El id de reserva es invalido");
        } catch (Exception ex) {
            escribirError(response, ex);
        }
    }

    /** Devuelve el codigo_qr del comprobante de la reserva, o "RES-{id}" si no existe. */
    private String obtenerCodigo(int idReserva) {
        String sql = "SELECT codigo_qr FROM COMPROBANTE_COMPRA WHERE id_reserva = ? LIMIT 1";
        try (Connection c = DBFactoryProvider.getManager().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String q = rs.getString(1);
                    if (q != null && !q.isBlank()) return q;
                }
            }
        } catch (Exception ignored) {
        }
        return "RES-" + idReserva;
    }

    private Image cargarLogo() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(LOGO)) {
            return (is == null) ? null : ImageIO.read(is);
        } catch (IOException e) {
            return null;
        }
    }
}
