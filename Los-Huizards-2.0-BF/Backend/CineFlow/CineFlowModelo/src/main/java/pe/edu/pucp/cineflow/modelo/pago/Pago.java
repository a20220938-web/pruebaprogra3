package pe.edu.pucp.cineflow.modelo.pago;
import pe.edu.pucp.cineflow.modelo.reserva.*;
import java.time.LocalDateTime;
import java.util.Random;

public class Pago {
    private int idPago;
    private double monto;
    private LocalDateTime fecha;
    private EstadoPago estado;
    private MetodoPago metodo;

    public Pago(){};
    public Pago(int idPago, double monto, LocalDateTime fecha, EstadoPago estado, MetodoPago metodo)  {
        this.idPago = idPago;
        this.monto = monto;
        this.fecha = fecha;
        this.estado = estado;
        this.metodo = metodo;
    }
    public int  getIdPago(){
        return idPago;
    }
    public double getMonto(){
        return monto;
    }
    public LocalDateTime getFecha(){
        return fecha;
    }
    public EstadoPago getEstado() {
        return estado;
    }
    public MetodoPago getMetodo(){
        return metodo;
    }
    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public void setMetodo(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public void iniciarPago() {
        this.estado = EstadoPago.PENDIENTE;
    }

   public boolean procesarPago() {
        this.estado = EstadoPago.PROCESANDO;
        System.out.println("Procesando pago con " + metodo);

        // Simulación de respuesta externa
        Random rand = new Random();
        boolean aprobado = rand.nextDouble() < 0.95;

        return aprobado;
    }

    public void actualizarEstado(boolean resultado) {
        if (resultado) {
            confirmarPago();
        } else {
            rechazarPago();
        }
    }

    public void confirmarPago() {
        this.estado = EstadoPago.APROBADO;
        System.out.println("Pago aprobado.");
    }

    public void rechazarPago() {
        this.estado = EstadoPago.FALLIDO;
        System.out.println("Pago rechazado.");
    }

    public boolean esPagoExitoso() {
        return this.estado == EstadoPago.APROBADO;
    }
}
