package pe.edu.pucp.cineflow.modelo.catalogo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;

public class Funcion {

    private int id;
    private LocalDateTime fechaHora;
    private double precioBase;

    private Pelicula pelicula;
    private Sala sala;
    private FormatoProyeccion formato;
    private List<Asiento> mapaAsientos;

    public Funcion() {
        this.mapaAsientos = new ArrayList<>();
    }

    public Funcion(int id, LocalDateTime fechaHora, double precioBase,
                   Pelicula pelicula, Sala sala, FormatoProyeccion formato) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.precioBase = precioBase;
        this.pelicula = pelicula;
        this.sala = sala;
        this.formato = formato;
        this.mapaAsientos = new ArrayList<>();
        generarMapaAsientos();
    }

    public Funcion(Funcion otro) {
        this.id = otro.id;
        this.fechaHora = otro.fechaHora;
        this.precioBase = otro.precioBase;
        this.pelicula = otro.pelicula;
        this.sala = otro.sala;
        this.formato = otro.formato;
        this.mapaAsientos = otro.mapaAsientos;
    }

    public void generarMapaAsientos() {
        if (sala == null) return;
        mapaAsientos.clear();

        int filas       = sala.getFilas() > 0 ? sala.getFilas() : 1;
        int colsPorFila = sala.getColumnasPorFila() > 0
                ? sala.getColumnasPorFila()
                : sala.getCapacidad();

        int asientoId = 1;
        for (int f = 0; f < filas && asientoId <= sala.getCapacidad(); f++) {
            char letraFila = (char) ('A' + f);
            for (int c = 1; c <= colsPorFila && asientoId <= sala.getCapacidad(); c++) {
                mapaAsientos.add(new Asiento(asientoId, letraFila, c, false, EstadoAsiento.DISPONIBLE, this));
                asientoId++;
            }
        }
    }

    public List<Asiento> getMapaAsientos() {
        return mapaAsientos;
    }

    public List<Asiento> obtenerAsientos() {
        return mapaAsientos;
    }

    public List<Asiento> obtenerAsientosDisponibles() {
        List<Asiento> disponibles = new ArrayList<>();
        if (mapaAsientos != null) {
            for (Asiento a : mapaAsientos) {
                if (a.getEstado() == EstadoAsiento.DISPONIBLE) {
                    disponibles.add(a);
                }
            }
        }
        return disponibles;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getPrecioBase() {
        return precioBase;
    }
    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }
    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }
    public void setSala(Sala sala) {
        this.sala = sala;
        if (this.mapaAsientos == null) this.mapaAsientos = new ArrayList<>();
        if (this.mapaAsientos.isEmpty()) generarMapaAsientos();
    }

    public FormatoProyeccion getFormato() {
        return formato;
    }
    public void setFormato(FormatoProyeccion formato) {
        this.formato = formato;
    }
}