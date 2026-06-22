package pe.edu.pucp.cineflow.rs.dto;

/**
 * Payload de entrada para el login por REST.
 * Necesita constructor sin argumentos + getters/setters para que JAX-RS
 * (JSON-B) lo pueda deserializar desde el cuerpo JSON.
 */
public class CredencialesLogin {
    private String email;
    private String contrasenia;

    public CredencialesLogin() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}
