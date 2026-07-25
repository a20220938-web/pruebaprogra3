package pe.edu.pucp.cineflow.modelo.reserva;


import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

public class InventarioCine {
    private int idInventario;
    private int stockActual;
    private int stockMinimo;
    private LocalDateTime ultimaReposicion;
    private List<MovimientoStock> movimientos = new ArrayList<>();
    private int idCine;

    // Constructor por defecto
    public InventarioCine() {}

    // Constructor con parámetros
    public InventarioCine(int idInventario, int stockActual, int stockMinimo, LocalDateTime ultimaReposicion) {
        this.idInventario = idInventario;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.ultimaReposicion = ultimaReposicion;
    }

    // Getters y Setters

    public int getIdCine() { return idCine; }
    public void setIdCine(int idCine) { this.idCine = idCine; }
    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public LocalDateTime getUltimaReposicion() {
        return ultimaReposicion;
    }

    public void setUltimaReposicion(LocalDateTime ultimaReposicion) {
        this.ultimaReposicion = ultimaReposicion;
    }

    public List<MovimientoStock> getMovimientos() {
        return new ArrayList<>(movimientos);
    }

    public void actualizarStock(int cantidad) {
        this.stockActual += cantidad;
        MovimientoStock movimiento = new MovimientoStock();
        movimiento.setTipo(cantidad > 0 ? "INGRESO" : "EGRESO");
        movimiento.setCantidad(Math.abs(cantidad));
        movimiento.registrarMovimiento();
        this.movimientos.add(movimiento);
    }

    public boolean verificarDisponibilidad(int cantidadRequerida) {
        return this.stockActual >= cantidadRequerida;
    }

    public void generarAlertaStockBajo() {
        if (this.stockActual < this.stockMinimo) {
            System.out.println("Alerta: Stock bajo en inventario ID " + idInventario);
        }
    }
}
