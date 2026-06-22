package pe.edu.pucp.cineflow.modelo.reporte;

import pe.edu.pucp.cineflow.modelo.reserva.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class ComprobanteCompra {

    private int idComprobante;
    private String codigoQR;
    private double montoTotal;
    private Reserva reserva;

    public ComprobanteCompra() {
    }

    public ComprobanteCompra(int idComprobante, String codigoQR, double montoTotal, Reserva reserva) {
        this.idComprobante = idComprobante;
        this.codigoQR = codigoQR;
        this.montoTotal = montoTotal;
        this.reserva = reserva;
    }

    public ComprobanteCompra(ComprobanteCompra otro) {
        this.idComprobante = otro.idComprobante;
        this.codigoQR = otro.codigoQR;
        this.montoTotal = otro.montoTotal;
        this.reserva = otro.reserva;
    }

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public String getCodigoQR() {
        return codigoQR;
    }

    public void setCodigoQR(String codigoQR) {
        this.codigoQR = codigoQR;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public File generarComprobante() {
        if (codigoQR == null || codigoQR.trim().isEmpty()) {
            generarQR();
        }

        DecimalFormat df = new DecimalFormat("0.00");
        File archivo = new File("comprobante_" + idComprobante + ".txt");

        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write("===== COMPROBANTE DE COMPRA =====\n");
            writer.write("ID Comprobante: " + idComprobante + "\n");
            writer.write("Monto total: S/ " + df.format(montoTotal) + "\n");
            writer.write("Codigo QR: " + codigoQR + "\n");

            if (reserva != null) {
                writer.write("ID Reserva: " + reserva.getIdReserva() + "\n");
            } else {
                writer.write("ID Reserva: no disponible\n");
            }

        } catch (IOException e) {
            System.out.println("Error al generar comprobante: " + e.getMessage());
        }

        return archivo;
    }

    public void generarQR() {
        this.codigoQR = "QR-" + idComprobante + "-" + System.currentTimeMillis();
    }

    public void visualizarComprobante() {
        DecimalFormat df = new DecimalFormat("0.00");

        System.out.println("===== COMPROBANTE DE COMPRA =====");
        System.out.println("ID Comprobante: " + idComprobante);
        System.out.println("Monto total: S/ " + df.format(montoTotal));
        System.out.println("Codigo QR: " + codigoQR);

        if (reserva != null) {
            System.out.println("Reserva asociada: " + reserva.getIdReserva());
        } else {
            System.out.println("Reserva asociada: no disponible");
        }
    }
}