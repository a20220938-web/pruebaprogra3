package pe.edu.pucp.cineflow.modelo.reserva;

public abstract class Confiteria {
    private int idItem;
    private String nombre;
    private String descripcion;
    private int cantidad;
    private double precioUnitario;

    public Confiteria() {
    }

    public Confiteria(int idItem, String nombre, String descripcion, int cantidad, double precioUnitario) {
        this.idItem = idItem;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters y Setters
    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public abstract double calcularSubtotal();

    public String obtenerDetalles() {
        return String.format("%s x%d - S/ %.2f", nombre, cantidad, calcularSubtotal());
    }
}