package pe.edu.pucp.cineflow.modelo.reserva;

public class ArticuloIndividual extends Confiteria {
    private String categoria;
    private int idInventario;

    public ArticuloIndividual() {
        super();
    }

    public ArticuloIndividual(int idItem, String nombre, int cantidad,
                              double precioUnitario, String categoria, int idInventario) {
        super(idItem, nombre, null, cantidad, precioUnitario);
        this.categoria = categoria;
        this.idInventario = idInventario;
    }

    public ArticuloIndividual(int idItem, String nombre, String descripcion, int cantidad,
                              double precioUnitario, String categoria, int idInventario) {
        super(idItem, nombre, descripcion, cantidad, precioUnitario);
        this.categoria = categoria;
        this.idInventario = idInventario;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getIdInventario() { return idInventario; }
    public void setIdInventario(int idInventario) { this.idInventario = idInventario; }
    @Override
    public double calcularSubtotal() {
        return getCantidad() * getPrecioUnitario();
    }
}