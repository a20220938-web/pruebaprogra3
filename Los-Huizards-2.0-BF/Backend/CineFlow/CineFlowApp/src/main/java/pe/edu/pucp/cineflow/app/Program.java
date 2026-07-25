package pe.edu.pucp.cineflow.app;

import pe.edu.pucp.cineflow.bo.catalogo.*;
import pe.edu.pucp.cineflow.bo.pago.IPagoBo;
import pe.edu.pucp.cineflow.bo.pago.PagoBoImpl;
import pe.edu.pucp.cineflow.bo.reporte.ComprobanteBoImpl;
import pe.edu.pucp.cineflow.bo.reporte.IComprobanteBo;
import pe.edu.pucp.cineflow.bo.reserva.ConfiteriaBoImpl;
import pe.edu.pucp.cineflow.bo.reserva.IConfiteriaBo;
import pe.edu.pucp.cineflow.bo.reserva.IInventarioCineBo;
import pe.edu.pucp.cineflow.bo.reserva.IReservaBo;
import pe.edu.pucp.cineflow.bo.reserva.InventarioCineBoImpl;
import pe.edu.pucp.cineflow.bo.reserva.ReservaBoImpl;
import pe.edu.pucp.cineflow.bo.usuario.IUsuarioBo;
import pe.edu.pucp.cineflow.bo.usuario.UsuarioBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.*;
import pe.edu.pucp.cineflow.modelo.pago.EstadoPago;
import pe.edu.pucp.cineflow.modelo.pago.MetodoPago;
import pe.edu.pucp.cineflow.modelo.pago.Pago;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;
import pe.edu.pucp.cineflow.modelo.reserva.*;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class Program {

    static final Random RNG = new Random();

    static String pick(String... opciones) {
        return opciones[RNG.nextInt(opciones.length)];
    }

    static <T> T pick(T[] valores) {
        return valores[RNG.nextInt(valores.length)];
    }

    static int entre(int min, int max) {
        return min + RNG.nextInt(max - min + 1);
    }

    static double entreD(double min, double max) {
        return Math.round((min + RNG.nextDouble() * (max - min)) * 100.0) / 100.0;
    }

    static Usuario crearUsuario() {
        String[] nombres   = {"Ana", "Luis", "Maria", "Carlos", "Sofia", "Diego", "Valeria", "Andres"};
        String[] apellidos = {"Garcia", "Lopez", "Torres", "Ramirez", "Flores", "Mendoza", "Castro"};
        String sufijo = String.valueOf(System.currentTimeMillis()).substring(8);

        Usuario u = new Usuario();
        u.setNombre(pick(nombres));
        u.setApellidos(pick(apellidos));
        u.setEmail(u.getNombre().toLowerCase() + sufijo + "@mail.com");
        u.setContrasenia("pass" + entre(1000, 9999));
        u.setTelefono("9" + entre(10000000, 99999999));
        u.setFechaNacimiento(LocalDate.of(entre(1980, 2004), entre(1, 12), entre(1, 28)));
        u.setFechaRegistro(LocalDateTime.now());
        return u;
    }

    static Pelicula crearPelicula() {
        String[] titulos = {
                "El Último Horizonte", "Sombras del Pasado", "Velocidad Infinita",
                "El Gran Engaño", "Más Allá del Universo", "La Trampa Perfecta",
                "Caos en la Ciudad", "El Guardián Secreto"
        };
        String[] sinopsis = {
                "Un viaje épico hacia lo desconocido.",
                "Secretos que nunca debieron salir a la luz.",
                "Cuando el tiempo se acaba, solo queda actuar.",
                "Una historia de lealtad y traición.",
                "El destino de la humanidad en manos de uno."
        };
        String[] restricciones = {"G", "PG", "PG-13", "14+", "18+"};

        Pelicula p = new Pelicula();
        p.setTitulo(pick(titulos) + " " + entre(1, 999));
        p.setDuracion(entre(80, 180));
        p.setSinopsis(pick(sinopsis));
        p.setGenero(pick(Genero.values()));
//        p.setEdadResticcion(pick(restricciones));
        return p;
    }

    static Cine crearCine() {
        String[] nombres   = {"CineFlow", "CineStar", "CinePlex", "CineMax", "CinePucp"};
        String[] distritos = {"Miraflores", "San Isidro", "Surco", "La Molina", "Barranco", "San Borja"};
        String[] avenidas  = {"Javier Prado", "Benavides", "Arequipa", "La Marina", "Universitaria"};

        Cine c = new Cine();
        c.setNombre(pick(nombres) + " " + pick(distritos));
        c.setUbicacion("Av. " + pick(avenidas) + " " + entre(100, 2000) + ", " + pick(distritos));
        return c;
    }

    static InventarioCine crearInventario(int idCine) {
        InventarioCine inv = new InventarioCine();
        inv.setStockActual(entre(50, 300));
        inv.setStockMinimo(entre(5, 20));
        inv.setUltimaReposicion(LocalDateTime.now().minusDays(entre(0, 30)));
        inv.setIdCine(idCine);
        return inv;
    }

    static ArticuloIndividual crearArticulo(int idInventario) {
        String[] nombres      = {"Palomitas", "Nachos", "Hot Dog", "Gaseosa", "Agua", "Combo Familiar"};
        String[] tamanios     = {"chico", "mediano", "grande"};
        String[] categorias   = {"COMIDA", "BEBIDA", "COMBO"};
        String[] descripciones = {
                "Crujientes y recién hechas.", "Con salsa y queso cheddar.",
                "Con mostaza y ketchup.", "Fría y refrescante.", "Natural sin gas.", "Ideal para compartir."
        };

        return new ArticuloIndividual(
                0,
                pick(nombres) + " " + pick(tamanios),
                pick(descripciones),
                entre(1, 5),
                entreD(5.0, 30.0),
                pick(categorias),
                idInventario
        );
    }

    static Sala crearSala(Cine cine, int numero) {
        Sala s = new Sala();
        s.setNumero(numero);
        int capacidad = entre(20, 100);
        int cols = entre(8, 12);
        int filas = (int) Math.ceil((double) capacidad / cols);
        s.setCapacidad(capacidad);
        s.setFilas(filas);
        s.setColumnasPorFila(cols);
        s.setCine(cine);
        return s;
    }

    static Funcion crearFuncion(Pelicula pelicula, Sala sala) {
        Integer[] horarios = {14, 16, 18, 20, 22};
        Funcion f = new Funcion();
        f.setPelicula(pelicula);
        f.setSala(sala);
        f.setFechaHora(LocalDateTime.now()
                .plusDays(entre(1, 14))
                .withHour(pick(horarios))
                .withMinute(0).withSecond(0).withNano(0));
        f.setPrecioBase(entreD(15.0, 40.0));
        f.setFormato(pick(FormatoProyeccion.values()));
        return f;
    }

    static Pago crearPago(double monto) {
        Pago p = new Pago();
        p.setMonto(monto);
        p.setFecha(LocalDateTime.now());
        p.setEstado(EstadoPago.APROBADO);
        p.setMetodo(pick(MetodoPago.values()));
        return p;
    }

    // Main

    public static void main(String[] args) {

        IUsuarioBo        usuarioBo     = new UsuarioBoImpl();
        IPeliculaBo       peliculaBo    = new PeliculaBoImpl();
        ICineBo           cineBo        = new CineBoImpl();
        ISalaBo           salaBo        = new SalaBoImpl();
        IFuncionBo        funcionBo     = new FuncionBoImpl();
        IPagoBo           pagoBo        = new PagoBoImpl();
        IReservaBo        reservaBo     = new ReservaBoImpl();
        IComprobanteBo    comprobanteBo = new ComprobanteBoImpl();
        IInventarioCineBo inventarioBo  = new InventarioCineBoImpl();
        IConfiteriaBo     confiteriaBo  = new ConfiteriaBoImpl();

        try {
            System.out.println("   CINEFLOW ");
            System.out.println("════════════════════════════════════════");

            // USUARIO
            Usuario usuario = crearUsuario();
            usuarioBo.guardar(usuario, Estado.Nuevo);
            System.out.println("\n[USUARIO]");
            System.out.println("  Nombre:  " + usuario.getNombre() + " " + usuario.getApellidos());
            System.out.println("  Email:   " + usuario.getEmail());
            System.out.println("  Teléfono:" + usuario.getTelefono());
            System.out.println("  ID:      " + usuario.getIdUsuario());

            // PELÍCULA
            Pelicula pelicula = crearPelicula();
            peliculaBo.guardar(pelicula, Estado.Nuevo);
            System.out.println("\n[PELÍCULA]");
            System.out.println("  Título:   " + pelicula.getTitulo());
            System.out.println("  Género:   " + pelicula.getGenero());
            System.out.println("  Duración: " + pelicula.getDuracion() + " min");
            System.out.println("  Edad:     " + pelicula.getEdadRestriccion());
            System.out.println("  ID:       " + pelicula.getId());

            // CINE
            Cine cine = crearCine();
            cineBo.guardar(cine, Estado.Nuevo);
            System.out.println("\n[CINE]");
            System.out.println("  Nombre:    " + cine.getNombre());
            System.out.println("  Ubicación: " + cine.getUbicacion());
            System.out.println("  ID:        " + cine.getId());

            // INVENTARIO
            InventarioCine inventario = crearInventario(cine.getId());
            inventarioBo.guardar(inventario, Estado.Nuevo);
            System.out.println("\n[INVENTARIO]");
            System.out.println("  Stock actual:  " + inventario.getStockActual());
            System.out.println("  Stock mínimo:  " + inventario.getStockMinimo());
            System.out.println("  Última repos.: " + inventario.getUltimaReposicion());
            System.out.println("  ID:            " + inventario.getIdInventario());

            // CONFITERÍA
            ArticuloIndividual articulo1 = crearArticulo(inventario.getIdInventario());
            ArticuloIndividual articulo2 = crearArticulo(inventario.getIdInventario());
            confiteriaBo.guardar(articulo1, Estado.Nuevo);
            confiteriaBo.guardar(articulo2, Estado.Nuevo);
            System.out.println("\n[CONFITERÍA]");
            System.out.println("  → " + articulo1.getNombre()
                    + " | Cat: " + articulo1.getCategoria()
                    + " | S/" + articulo1.getPrecioUnitario()
                    + " | ID: " + articulo1.getIdItem());
            System.out.println("  → " + articulo2.getNombre()
                    + " | Cat: " + articulo2.getCategoria()
                    + " | S/" + articulo2.getPrecioUnitario()
                    + " | ID: " + articulo2.getIdItem());

            // SALA
            Sala sala = crearSala(cine, entre(1, 20));
            salaBo.guardar(sala, Estado.Nuevo);
            System.out.println("\n[SALA]");
            System.out.println("  Número:    " + sala.getNumero());
            System.out.println("  Capacidad: " + sala.getCapacidad());
            System.out.println("  ID:        " + sala.getId());

            // FUNCIÓN
            Funcion funcion = crearFuncion(pelicula, sala);
            funcionBo.guardar(funcion, Estado.Nuevo);
            System.out.println("\n[FUNCIÓN]");
            System.out.println("  Película:  " + pelicula.getTitulo());
            System.out.println("  Formato:   " + funcion.getFormato());
            System.out.println("  FechaHora: " + funcion.getFechaHora());
            System.out.println("  Precio:    S/" + funcion.getPrecioBase());
            System.out.println("  Asientos:  " + funcion.getMapaAsientos().size());
            System.out.println("  ID:        " + funcion.getId());

            // PAGO
            double totalPago = Math.round(
                    (funcion.getPrecioBase() * 2 + articulo1.getPrecioUnitario()) * 100.0) / 100.0;
            Pago pago = crearPago(totalPago);
            pagoBo.guardar(pago, Estado.Nuevo);
            System.out.println("\n[PAGO]");
            System.out.println("  Monto:  S/" + pago.getMonto());
            System.out.println("  Método: " + pago.getMetodo());
            System.out.println("  Estado: " + pago.getEstado());
            System.out.println("  ID:     " + pago.getIdPago());

            // RESERVA
            List<Asiento> asientosSeleccionados = funcion.getMapaAsientos()
                    .subList(0, Math.min(2, funcion.getMapaAsientos().size()));

            Reserva reserva = new Reserva();
            reserva.setUsuario(usuario);
            reserva.setPago(pago);
            reserva.setFuncion(funcion);
            reserva.setAsientos(asientosSeleccionados);
            reserva.setConfiteria(articulo1);
            reserva.setEntradas(List.of(
                    new Entrada(0, funcion.getPrecioBase(), pick(tipoEntrada.values())),
                    new Entrada(0, funcion.getPrecioBase(), pick(tipoEntrada.values()))
            ));

            reservaBo.guardar(reserva, Estado.Nuevo);
            reservaBo.confirmarReserva(reserva.getIdReserva());
            Reserva reservaLeida = reservaBo.obtener(reserva.getIdReserva());
            System.out.println("\n[RESERVA]");
            System.out.println("  ID:     " + reserva.getIdReserva());
            System.out.println("  Estado: " + (reservaLeida != null ? reservaLeida.getEstado() : "null"));
            System.out.println("  Total:  S/" + (reservaLeida != null ? reservaLeida.getTotalFinal() : "null"));

            // COMPROBANTE
            ComprobanteCompra comprobante = reservaBo.generarComprobante(reserva.getIdReserva());
            System.out.println("\n[COMPROBANTE]");
            System.out.println("  ID: " + comprobante.getIdComprobante());
            System.out.println("  QR: " + comprobante.getCodigoQR());
            comprobante.visualizarComprobante();

            // ── 11. RESUMEN ─────────────────────────────────────────────────
            System.out.println("\n════════════════════════════════════════");
            System.out.println("   RESUMEN - Cantidad en bd");
            System.out.println("════════════════════════════════════════");
            System.out.println("Usuarios:      " + usuarioBo.listar().size());
            System.out.println("Películas:     " + peliculaBo.listar().size());
            System.out.println("Funciones:     " + funcionBo.listar().size());
            System.out.println("Comprobantes:  " + comprobanteBo.listar().size());
            System.out.println("Artículos en inventario " + inventario.getIdInventario()
                    + ": " + confiteriaBo.listarPorInventario(inventario.getIdInventario()).size());

        } catch (Exception e) {
            System.out.println("\n✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}