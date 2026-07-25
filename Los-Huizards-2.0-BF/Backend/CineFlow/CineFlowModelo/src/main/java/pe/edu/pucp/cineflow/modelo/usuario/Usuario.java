package pe.edu.pucp.cineflow.modelo.usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;

public class Usuario {
    private int idUsuario;
    private String email;
    private String contrasenia;
    private String nombre;
    private String apellidos;
    private String telefono;
    private LocalDate fechaNacimiento;
    private LocalDateTime fechaRegistro;
    private boolean sesionActiva = false;
    private List<Reserva> historialReservas = new ArrayList<>();

    // Constructor sin parámetros
    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // Constructor con parámetros (todos los atributos)
    public Usuario(int idUsuario, String email, String contrasenia, String nombre,
                   String apellidos, String telefono, LocalDate fechaNacimiento,
                   LocalDateTime fechaRegistro) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.contrasenia = contrasenia;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaRegistro = fechaRegistro;
    }

    // Constructor copia
    public Usuario(Usuario otro) {
        this.idUsuario = otro.idUsuario;
        this.email = otro.email;
        this.contrasenia = otro.contrasenia;
        this.nombre = otro.nombre;
        this.apellidos = otro.apellidos;
        this.telefono = otro.telefono;
        this.fechaNacimiento = (otro.fechaNacimiento != null) ? LocalDate.from(otro.fechaNacimiento) : null;
        this.fechaRegistro = (otro.fechaRegistro != null) ? LocalDateTime.from(otro.fechaRegistro) : null;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean registrar(String email, String contrasenia) {
        if (email == null || email.trim().isEmpty()) return false;
        if (contrasenia == null || contrasenia.trim().isEmpty()) return false;
        if (!email.contains("@") || !email.contains(".")) return false;
        if (contrasenia.length() < 6) return false;
        this.email = email;
        this.contrasenia = contrasenia;
        this.fechaRegistro = LocalDateTime.now();
        return true;
    }

    public boolean iniciarSesion(String email, String contrasenia) {
        if (email == null || email.trim().isEmpty()) return false;
        if (contrasenia == null || contrasenia.trim().isEmpty()) return false;
        if (this.email == null || this.contrasenia == null) return false;
        if (!this.email.equals(email)) return false;
        if (!this.contrasenia.equals(contrasenia)) return false;
        this.sesionActiva = true;
        return true;
    }

    public void cerrarSesion() {
        this.sesionActiva = false;
    }

    public boolean recuperarContrasenia(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        if (this.email == null) return false;
        return this.email.equals(email);
    }

    public boolean editarPerfil(String nombre, String apellidos, String telefono, LocalDate fechaNacimiento) {
        if (!this.sesionActiva) return false;
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (apellidos == null || apellidos.trim().isEmpty()) return false;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        return true;
    }

    public List<Reserva> consultarHistorialReservas() {
        if (!this.sesionActiva) return null;
        return new ArrayList<>(this.historialReservas);
    }

    public File descargarComprobantePDF(int idReserva) {
        if (idReserva <= 0) return null;
        if (!this.sesionActiva) return null;
        File archivo = new File("comprobante_reserva_" + idReserva + ".txt");
        try (FileWriter writer = new FileWriter(archivo)) {
            writer.write("===== COMPROBANTE DE COMPRA =====\n");
            writer.write("ID Reserva: " + idReserva + "\n");
            writer.write("Usuario: " + (this.nombre != null ? this.nombre : "") +
                    " " + (this.apellidos != null ? this.apellidos : "") + "\n");
            writer.write("Email: " + (this.email != null ? this.email : "") + "\n");
            writer.write("Fecha de descarga: " + LocalDateTime.now() + "\n");
            writer.write("=================================\n");
        } catch (IOException e) {
            return null;
        }
        return archivo;
    }

    public int calcularEdad() {
        if (this.fechaNacimiento == null) return 0;
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }

    public boolean isSesionActiva() { return sesionActiva; }

    public void agregarReserva(Reserva reserva) {
        if (reserva != null) this.historialReservas.add(reserva);
    }
}
