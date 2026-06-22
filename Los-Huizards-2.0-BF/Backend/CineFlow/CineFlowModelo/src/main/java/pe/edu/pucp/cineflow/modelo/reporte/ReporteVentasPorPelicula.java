package pe.edu.pucp.cineflow.modelo.reporte;

import pe.edu.pucp.cineflow.modelo.reserva.*;
import pe.edu.pucp.cineflow.modelo.catalogo.*;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReporteVentasPorPelicula {

    private int idReporte;
    private int cantidadEntradasVendidas;
    private double montoTotalRecaudado;
    private Pelicula pelicula;
    private List<Reserva> reservas;

    public ReporteVentasPorPelicula() {
        this.reservas = new ArrayList<>();
    }

    public ReporteVentasPorPelicula(int idReporte, Pelicula pelicula, List<Reserva> reservas) {
        this.idReporte = idReporte;
        this.pelicula = pelicula;
        this.reservas = new ArrayList<>();

        if (reservas != null) {
            this.reservas.addAll(reservas);
        }

        recalcularDatos();
    }

    public ReporteVentasPorPelicula(ReporteVentasPorPelicula otro) {
        this.idReporte = otro.idReporte;
        this.cantidadEntradasVendidas = otro.cantidadEntradasVendidas;
        this.montoTotalRecaudado = otro.montoTotalRecaudado;
        this.pelicula = otro.pelicula;
        this.reservas = new ArrayList<>(otro.reservas);
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public int getCantidadEntradasVendidas() {
        return cantidadEntradasVendidas;
    }

    public void setCantidadEntradasVendidas(int cantidadEntradasVendidas) {
        this.cantidadEntradasVendidas = cantidadEntradasVendidas;
    }

    public double getMontoTotalRecaudado() {
        return montoTotalRecaudado;
    }

    public void setMontoTotalRecaudado(double montoTotalRecaudado) {
        this.montoTotalRecaudado = montoTotalRecaudado;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = new ArrayList<>();
        if (reservas != null) {
            this.reservas.addAll(reservas);
        }
        recalcularDatos();
    }

    public void agregarReserva(Reserva reserva) {
        if (reserva != null) {
            this.reservas.add(reserva);
            recalcularDatos();
        }
    }

    private void recalcularDatos() {
        cantidadEntradasVendidas = 0;
        montoTotalRecaudado = 0.0;

        for (Reserva reserva : reservas) {
            if (reserva != null) {
                if (reserva.getEntradas() != null)
                    cantidadEntradasVendidas += reserva.getEntradas().size(); // cuenta entradas reales
                montoTotalRecaudado += reserva.getTotalFinal();
            }
        }
    }

    public void visualizarReporte() {
        DecimalFormat df = new DecimalFormat("0.00");

        System.out.println("===== REPORTE DE VENTAS POR PELICULA =====");
        System.out.println("ID Reporte: " + idReporte);

        if (pelicula != null) {
            System.out.println("Pelicula: " + pelicula.getTitulo());
        } else {
            System.out.println("Pelicula: no disponible");
        }

        System.out.println("Cantidad de entradas vendidas: " + cantidadEntradasVendidas);
        System.out.println("Monto total recaudado: S/ " + df.format(montoTotalRecaudado));
    }
}