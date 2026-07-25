package pe.edu.pucp.cineflow.rs.util;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class StripeService {

    private static final String secretKey = cargarSecretKey();

    private static String cargarSecretKey() {
        Properties props = new Properties();
        try (InputStream is = StripeService.class.getClassLoader()
                .getResourceAsStream("stripe.properties")) {
            if (is != null) props.load(is);
        } catch (Exception e) {
            System.err.println("[StripeService] No se pudo cargar stripe.properties: " + e.getMessage());
        }
        return props.getProperty("stripe.secret.key", "");
    }

    private static void desactivarSSLVerification() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] c, String a) {}
                public void checkServerTrusted(X509Certificate[] c, String a) {}
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }}, new java.security.SecureRandom());
            SSLContext.setDefault(ctx);
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((h, s) -> true);
        } catch (Exception e) {
            System.err.println("[StripeService] No se pudo desactivar SSL: " + e.getMessage());
        }
    }

    // Tarjeta de prueba que simula rechazo: 4000000000000002
    private static final String TARJETA_FALLO = "4000000000000002";

    public static boolean cobrar(double montoSoles, String descripcion, String numeroTarjeta) throws Exception {
        if (secretKey.isEmpty() || secretKey.equals("TU_STRIPE_SECRET_KEY")) {
            throw new Exception("Stripe no está configurado. Agrega tu secret key en stripe.properties");
        }

        desactivarSSLVerification();
        Stripe.apiKey = secretKey;

        long montoCentavos = Math.round(montoSoles * 100);

        // pm_card_chargeDeclined simula rechazo; pm_card_visa siempre aprueba
        String paymentMethod = TARJETA_FALLO.equals(numeroTarjeta)
                ? "pm_card_chargeDeclined"
                : "pm_card_visa";

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(montoCentavos)
                .setCurrency("pen")
                .setPaymentMethod(paymentMethod)
                .setConfirm(true)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                .build()
                )
                .setDescription(descripcion)
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        return "succeeded".equals(intent.getStatus());
    }

    public static boolean cobrar(double montoSoles, String descripcion) throws Exception {
        return cobrar(montoSoles, descripcion, "");
    }

    public static boolean cobrarBilleteraDigital(double montoSoles) {
        return true;
    }
}
