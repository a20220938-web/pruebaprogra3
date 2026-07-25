package pe.edu.pucp.cineflow.reportes;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import pe.edu.pucp.cineflow.db.DBFactoryProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Map;

/**
 * Base de los servlets de reportes (enfoque del profesor): carga el .jasper
 * PRECOMPILADO en Jaspersoft Studio y lo llena con la conexion del DBManager.
 * Ya NO compila .jrxml en tiempo de ejecucion.
 */
public abstract class JasperReporteBase extends HttpServlet {

    /** Carga el .jasper precompilado del classpath. 'nombre' va SIN extension. */
    protected JasperReport obtenerReporte(String nombre) throws Exception {
        String ruta = "reportes/" + nombre + ".jasper";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(ruta)) {
            if (is == null) {
                throw new IOException("No se encontro el reporte compilado: " + ruta
                        + ". Genera el .jasper en Jaspersoft Studio y dejalo en "
                        + "src/main/resources/reportes/");
            }
            return (JasperReport) JRLoader.loadObject(is);
        }
    }

    /** Genera una imagen QR a partir de un texto (ZXing). Devuelve null si falla. */
    protected BufferedImage generarQr(String texto, int tam) {
        try {
            BitMatrix matrix = new QRCodeWriter()
                    .encode(texto == null ? "" : texto, BarcodeFormat.QR_CODE, tam, tam);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            return null;
        }
    }

    protected void generarPdf(HttpServletResponse response, JasperReport reporte,
                              Map<String, Object> parametros) throws IOException {
        try (Connection conn = DBFactoryProvider.getManager().getConnection()) {
            JasperPrint jp = JasperFillManager.fillReport(reporte, parametros, conn);
            response.setContentType("application/pdf");
            JasperExportManager.exportReportToPdfStream(jp, response.getOutputStream());
        } catch (Exception ex) {
            escribirError(response, ex);
        }
    }

    protected void escribirError(HttpServletResponse response, Throwable ex) throws IOException {
        response.reset();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain; charset=UTF-8");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("=== ERROR AL GENERAR EL REPORTE ===");
        Throwable t = ex; int n = 0;
        while (t != null) {
            pw.println((n == 0 ? "" : "Caused by: ") + t.getClass().getName() + ": " + t.getMessage());
            t = t.getCause(); n++;
        }
        pw.println();
        pw.println("--- Stack trace completo ---");
        ex.printStackTrace(pw);
        pw.flush();
        response.getWriter().write(sw.toString());
    }
}
