package pe.edu.pucp.cineflow.rs.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class EmailService {

    private static final Properties config = cargarConfig();

    private static Properties cargarConfig() {
        Properties props = new Properties();
        try (InputStream is = EmailService.class.getClassLoader()
                .getResourceAsStream("email.properties")) {
            if (is != null) props.load(is);
        } catch (Exception e) {
            System.err.println("[EmailService] No se pudo cargar email.properties: " + e.getMessage());
        }
        return props;
    }

    public static String getFrontendUrl() {
        return config.getProperty("frontend.url", "http://localhost:5168");
    }

    public static void enviarResetPassword(String destinatario, String token) throws Exception {
        String apiKey = config.getProperty("brevo.api.key");

        if (apiKey == null || apiKey.startsWith("TU_")) {
            throw new Exception("API key de Brevo no configurada en email.properties");
        }

        String remitente = config.getProperty("mail.remitente", "noreply@cineflow.com");
        String nombreRemitente = "CineFlow";
        String resetLink = getFrontendUrl() + "/restablecer-contrasenia?token=" + token;

        String body = "{"
            + "\"sender\":{\"name\":\"" + nombreRemitente + "\",\"email\":\"" + remitente + "\"},"
            + "\"to\":[{\"email\":\"" + destinatario + "\"}],"
            + "\"subject\":\"Restablecer contraseña – CineFlow\","
            + "\"htmlContent\":\"" + buildHtml(resetLink).replace("\"", "\\\"").replace("\n", "") + "\""
            + "}";

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] c, String a) {}
            public void checkServerTrusted(X509Certificate[] c, String a) {}
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        }}, new java.security.SecureRandom());

        HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
            .header("accept", "application/json")
            .header("api-key", apiKey)
            .header("content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new Exception("Brevo API error " + response.statusCode() + ": " + response.body());
        }

        System.out.println("[EmailService] Correo enviado a: " + destinatario);
    }

    private static String buildHtml(String resetLink) {
        return "<!DOCTYPE html>"
            + "<html><body style='font-family:Arial,sans-serif;background:#1a1a2e;color:#eee;padding:30px;'>"
            + "<div style='max-width:500px;margin:0 auto;background:#16213e;padding:30px;"
            + "border-radius:10px;border:1px solid #e53935;'>"
            + "<h1 style='color:#e53935;text-align:center;'>CineFlow</h1>"
            + "<h2 style='text-align:center;'>Restablecer Contrasena</h2>"
            + "<p>Hemos recibido una solicitud para restablecer la contrasena de tu cuenta.</p>"
            + "<p>Haz clic en el boton para crear una nueva contrasena. El enlace expira en <strong>30 minutos</strong>.</p>"
            + "<div style='text-align:center;margin:30px 0;'>"
            + "<a href='" + resetLink + "' style='background:#e53935;color:white;padding:12px 30px;"
            + "text-decoration:none;border-radius:25px;font-weight:bold;display:inline-block;'>"
            + "Restablecer Contrasena</a></div>"
            + "<p>O copia este enlace en tu navegador:</p>"
            + "<p style='word-break:break-all;color:#aaa;font-size:12px;'>" + resetLink + "</p>"
            + "<hr style='border-color:#333;'/>"
            + "<p style='color:#777;font-size:11px;'>Si no solicitaste este cambio, ignora este correo.</p>"
            + "</div></body></html>";
    }
}
