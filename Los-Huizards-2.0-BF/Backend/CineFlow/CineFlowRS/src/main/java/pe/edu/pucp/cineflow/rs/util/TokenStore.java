package pe.edu.pucp.cineflow.rs.util;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacén en memoria de tokens de recuperación de contraseña.
 * Singleton — los tokens expiran a los 30 minutos.
 */
public class TokenStore {

    private static final TokenStore INSTANCE = new TokenStore();
    private final Map<String, TokenEntry> tokens = new ConcurrentHashMap<>();

    private TokenStore() {}

    public static TokenStore getInstance() {
        return INSTANCE;
    }

    /** Genera un token UUID para el email dado y lo guarda (invalida el anterior si existía). */
    public String generarToken(String email) {
        tokens.entrySet().removeIf(e -> e.getValue().email.equalsIgnoreCase(email));
        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenEntry(email, LocalDateTime.now().plusMinutes(30)));
        return token;
    }

    /** Devuelve el email asociado al token, o null si no existe o expiró. */
    public String getEmail(String token) {
        TokenEntry entry = tokens.get(token);
        if (entry == null) return null;
        if (LocalDateTime.now().isAfter(entry.expiry)) {
            tokens.remove(token);
            return null;
        }
        return entry.email;
    }

    /** Invalida el token tras el uso exitoso. */
    public void invalidar(String token) {
        tokens.remove(token);
    }

    private static class TokenEntry {
        final String email;
        final LocalDateTime expiry;

        TokenEntry(String email, LocalDateTime expiry) {
            this.email = email;
            this.expiry = expiry;
        }
    }
}
