package pe.edu.pucp.cineflow.modelo.reporte;

import pe.edu.pucp.cineflow.modelo.reserva.*;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;
import pe.edu.pucp.cineflow.modelo.catalogo.Sala;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;

import java.text.DecimalFormat;
import java.util.List;

public class ResumenCompra {

    private int idResumen;
    private double total;
    private Reserva reserva;

    public ResumenCompra() {
    }

    public ResumenCompra(int idResumen, double total, Reserva reserva) {
        this.idResumen = idResumen;
        this.total = total;
        this.reserva = reserva;
    }

    public ResumenCompra(ResumenCompra otro) {
        this.idResumen = otro.idResumen;
        this.total = otro.total;
        this.reserva = otro.reserva;
    }

    public int getIdResumen() { return idResumen; }
    public void setIdResumen(int idResumen) { this.idResumen = idResumen; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public void visualizarResumen() {
        DecimalFormat df = new DecimalFormat("0.00");

        System.out.println("===== RESUMEN DE COMPRA =====");
        System.out.println("ID Resumen: " + idResumen);

        if (reserva == null) {
            System.out.println("Reserva asociada: no disponible");
            return;
        }

        System.out.println("ID Reserva: " + reserva.getIdReserva());
        System.out.println("Fecha reserva: " + reserva.getFechaReserva());
        System.out.println("Estado: " + reserva.getEstado());

        // --- Usuario ---
        Usuario usuario = reserva.getUsuario();
        if (usuario != null) {
            System.out.println("Cliente: " + usuario.getNombre() + " " + usuario.getApellidos());
        } else {
            System.out.println("Cliente: no disponible");
        }

        // --- Función: horario, película, sala, formato (via reserva.getFuncion()) ---
        Funcion funcion = reserva.getFuncion();
        if (funcion != null) {
            System.out.println("Horario función: " + funcion.getFechaHora());

            if (funcion.getPelicula() != null)
                System.out.println("Película: " + funcion.getPelicula().getTitulo());

            Sala sala = funcion.getSala();
            if (sala != null)
                System.out.println("Sala: " + sala.getNumero()
                    + " (capacidad: " + sala.getCapacidad() + ")");

            if (funcion.getFormato() != null)
                System.out.println("Formato: " + funcion.getFormato()); // imprime "IMAX" directamente
        } else {
            System.out.println("Función: no disponible");
        }

        // --- Entradas (tipo y precio) ---
        List<Entrada> entradas = reserva.getEntradas();
        if (entradas != null && !entradas.isEmpty()) {
            System.out.println("--- Entradas (" + entradas.size() + ") ---");
            for (Entrada e : entradas) {
                System.out.println("  Tipo: " + e.getTipo()
                    + " | Precio base: S/ " + df.format(e.getPrecioBase()));
            }
        } else {
            System.out.println("Entradas: no disponibles");
        }

        // --- Asientos seleccionados ---
        List<Asiento> asientos = reserva.getAsientos();
        if (asientos != null && !asientos.isEmpty()) {
            System.out.print("Asientos: ");
            for (Asiento a : asientos)
                System.out.print(a.getFila() + "" + a.getNumero() + " ");
            System.out.println();
        } else {
            System.out.println("Asientos: no disponibles");
        }

        // --- Confitería ---
        Confiteria conf = reserva.getConfiteria();
        if (conf != null) {
            System.out.println("Confiteria: cantidad " + conf.getCantidad());
        }

        // --- Total ---
        System.out.println("-----------------------------");
        System.out.println("TOTAL: S/ " + df.format(total));
        System.out.println("=============================");
    }
}
