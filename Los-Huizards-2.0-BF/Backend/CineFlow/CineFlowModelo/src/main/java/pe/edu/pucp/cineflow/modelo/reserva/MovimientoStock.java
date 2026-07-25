package pe.edu.pucp.cineflow.modelo.reserva;


import java.time.LocalDateTime;

public class MovimientoStock {
    private int idMovimiento;
    private String tipo; // INGRESO/EGRESO
    private int cantidad;
    private LocalDateTime fecha;
    private String motivo;

    // Constructor por defecto
    public MovimientoStock() {}

    // Constructor con parámetros
    public MovimientoStock(int idMovimiento, String tipo, int cantidad, LocalDateTime fecha, String motivo) {
        this.idMovimiento = idMovimiento;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.motivo = motivo;
    }

    // Getters y Setters
    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void registrarMovimiento() {
        this.fecha = LocalDateTime.now();
        System.out.println("Movimiento registrado: " + tipo + " de " + cantidad + " unidades.");
    }
}
