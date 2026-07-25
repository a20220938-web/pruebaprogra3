package pe.edu.pucp.cineflow.modelo.reserva;

import pe.edu.pucp.cineflow.modelo.usuario.*;
import pe.edu.pucp.cineflow.modelo.pago.*;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;

import java.time.LocalDateTime;
import java.util.List;

public class Reserva {

    private int idReserva;
    private LocalDateTime fechaReserva;
    private EstadoReserva estado;
    private double totalFinal;
    private LocalDateTime fechaExpiracion;

    private Usuario usuario;
    private List<Entrada> entradas;
    private Confiteria confiteria;
    private List<ArticuloIndividual> confiterias;  // NUEVO: varios snacks por reserva
    private List<Asiento> asientos;
    private Pago pago;
    private Funcion funcion;   // NUEVO: relación directa con la función

    private InventarioCine inventario;

    public Reserva() {
    }

    public Reserva(int idReserva, LocalDateTime fechaReserva, EstadoReserva estado, double totalFinal,
                   LocalDateTime fechaExpiracion, Usuario usuario, List<Entrada> entradas,
                   Confiteria confiteria, List<Asiento> asientos, Pago pago, Funcion funcion) {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.totalFinal = totalFinal;
        this.fechaExpiracion = fechaExpiracion;
        this.usuario = usuario;
        this.entradas = entradas;
        this.confiteria = confiteria;
        this.asientos = asientos;
        this.pago = pago;
        this.funcion = funcion;
    }

    public Reserva(Reserva otro) {
        this.idReserva = otro.idReserva;
        this.fechaReserva = otro.fechaReserva;
        this.estado = otro.estado;
        this.totalFinal = otro.totalFinal;
        this.fechaExpiracion = otro.fechaExpiracion;
        this.usuario = otro.usuario;
        this.entradas = otro.entradas;
        this.confiteria = otro.confiteria;
        this.asientos = otro.asientos;
        this.pago = otro.pago;
        this.funcion = otro.funcion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public boolean estaExpira() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }

    public boolean validarEdad() {
        if (usuario == null) return false;
        pe.edu.pucp.cineflow.modelo.catalogo.Funcion f = getFuncion();
        if (f == null || f.getPelicula() == null) return true;
        return f.getPelicula().esAptaPara(usuario.calcularEdad());
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public double getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(double totalFinal) {
        this.totalFinal = totalFinal;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<Entrada> entradas) {
        this.entradas = entradas;
    }

    public Confiteria getConfiteria() {
        return confiteria;
    }

    public void setConfiteria(Confiteria confiteria) {
        this.confiteria = confiteria;
    }

    public List<ArticuloIndividual> getConfiterias() {
        return confiterias;
    }

    public void setConfiterias(List<ArticuloIndividual> confiterias) {
        this.confiterias = confiterias;
    }

    public List<Asiento> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<Asiento> asientos) {
        this.asientos = asientos;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public pe.edu.pucp.cineflow.modelo.catalogo.Funcion getFuncion() {
        // Si se asignó directamente, la retornamos; si no, fallback a través de asientos
        if (funcion != null) return funcion;
        if (asientos != null && !asientos.isEmpty()) {
            return asientos.get(0).getFuncion();
        }
        return null;
    }

    public void setFuncion(pe.edu.pucp.cineflow.modelo.catalogo.Funcion funcion) {
        this.funcion = funcion;
    }


    public InventarioCine getInventario() { return inventario; }
    public void setInventario(InventarioCine inventario) { this.inventario = inventario; }

    public boolean validarConInventario(InventarioCine inventario, int cantidadRequerida) {
        this.inventario = inventario;
        return inventario.verificarDisponibilidad(cantidadRequerida);
    }

    public double calcularTotal() {
        double total = 0.0;
        if (entradas != null) {
            for (Entrada e : entradas) {
                total += e.calcularPrecio();
            }
        }
        if (confiteria != null) {
            total += confiteria.calcularSubtotal();
        }
        if (confiterias != null) {
            for (ArticuloIndividual c : confiterias) {
                total += c.calcularSubtotal();
            }
        }
        this.totalFinal = total;
        return total;
    }

    public void confirmarCompra() {
        this.estado = EstadoReserva.CONFIRMADA;
        if (asientos != null) {
            for (Asiento a : asientos) {
                a.cambiarEstado(EstadoAsiento.OCUPADO);
            }
        }
    }
}