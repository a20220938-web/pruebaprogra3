package pe.edu.pucp.cineflow.bo;

import pe.edu.pucp.cineflow.modelo.Estado;

import java.util.Objects;

public abstract class BaseBo {

    protected void validarIdPositivo(int id, String nombreCampo) {
        if (id <= 0)
            throw new IllegalArgumentException("El " + nombreCampo + " debe ser mayor a 0");
    }

    protected void validarTextoObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank())
            throw new IllegalArgumentException("El " + nombreCampo + " es obligatorio");
    }

    protected void validarMontoPositivo(double monto, String nombreCampo) {
        if (monto < 0)
            throw new IllegalArgumentException("El " + nombreCampo + " no puede ser negativo");
    }

    protected void validarEstado(Estado estado) {
        Objects.requireNonNull(estado, "El estado es obligatorio");
    }
}