package pe.edu.pucp.cineflow.modelo.catalogo;

import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;

import java.util.ArrayList;
import java.util.List;

public class Sala {

    private int id;
    private int numero;
    private int capacidad;
    private int filas;
    private int columnasPorFila;
    private Cine cine;

    public Sala() {}

    public Sala(int id, int numero, int capacidad, int filas, int columnasPorFila, Cine cine) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.filas = filas;
        this.columnasPorFila = columnasPorFila;
        this.cine = cine;
    }

    public Sala(Sala otro) {
        this.id = otro.id;
        this.numero = otro.numero;
        this.capacidad = otro.capacidad;
        this.filas = otro.filas;
        this.columnasPorFila = otro.columnasPorFila;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public int getFilas() { return filas; }
    public void setFilas(int filas) { this.filas = filas; }

    public int getColumnasPorFila() { return columnasPorFila; }
    public void setColumnasPorFila(int columnasPorFila) { this.columnasPorFila = columnasPorFila; }

    public Cine getCine() { return cine; }
    public void setCine(Cine cine) { this.cine = cine; }

    // -----------------------------------------------------------------------
    // Método implementado
    // -----------------------------------------------------------------------

    /**
     * Genera e imprime en consola la lista de todos los asientos de la sala,
     * cada uno con su fila, número y estado DISPONIBLE por defecto.
     * Los asientos se distribuyen en filas de 10 (A, B, C…).
     * Imprime también un resumen: sala, cine y capacidad total.
     */
    public void obtenerAsientos() {
        System.out.println("===== ASIENTOS — Sala " + numero
                + (cine != null ? " | Cine: " + cine.getNombre() : "") + " =====");
        System.out.println("Capacidad total: " + capacidad);
        System.out.println("-------------------------------");

        int asientosPorFila = 10;
        int filaActual = 0;
        char letraFila = 'A';

        for (int i = 1; i <= capacidad; i++) {
            int posEnFila = ((i - 1) % asientosPorFila) + 1;

            if (i > 1 && (i - 1) % asientosPorFila == 0) {
                letraFila++;
                System.out.println(); // salto de línea al cambiar de fila
            }

            System.out.printf("  [%c%02d: %-11s]", letraFila, posEnFila, EstadoAsiento.DISPONIBLE);
        }

        System.out.println();
        System.out.println("===============================");
    }
}