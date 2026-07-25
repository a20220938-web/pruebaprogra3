package pe.edu.pucp.cineflow.modelo.catalogo;

import java.util.ArrayList;
import java.util.List;

public class FiltroBusqueda {

    private String nombre;
    private String horario;
    private FormatoProyeccion formato;
    private String cine;
    private Genero genero;
    private String edadRestriccion;

    public FiltroBusqueda() {}

    public FiltroBusqueda(String nombre, String horario, FormatoProyeccion formato, String cine) {
        this.nombre = nombre;
        this.horario = horario;
        this.formato = formato;
        this.cine = cine;
    }

    public FiltroBusqueda(FiltroBusqueda otro) {
        this.nombre = otro.nombre;
        this.horario = otro.horario;
        this.formato = otro.formato;
        this.cine = otro.cine;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public FormatoProyeccion getFormato() { return formato; }
    public void setFormato(FormatoProyeccion formato) { this.formato = formato; }

    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }

    public String getCine() { return cine; }
    public void setCine(String cine) { this.cine = cine; }

    public String getEdadRestriccion() { return edadRestriccion; }
    public void setEdadResticcion(String edadRestriccion) { this.edadRestriccion = edadRestriccion; }


    public void reiniciarFiltros() {
        this.nombre = null;
        this.horario = null;
        this.formato = null;
        this.cine = null;
        this.genero = null;
        this.edadRestriccion = null;
    }


    public List<Pelicula> aplicarFiltro(Cine cineObj) {
        List<Pelicula> resultado = new ArrayList<>();

        if (cineObj == null || cineObj.getCartelera() == null) {
            return resultado;
        }

        // Filtro por nombre de cine
        if (cine != null && !cine.isBlank()) {
            if (!cineObj.getNombre().toLowerCase().contains(cine.toLowerCase())) {
                return resultado; // el cine no coincide → lista vacía
            }
        }

        List<Pelicula> candidatas = cineObj.getCartelera().obtenerPeliculas();

        for (Pelicula p : candidatas) {

            // Filtro por título
            if (nombre != null && !nombre.isBlank()) {
                if (!p.getTitulo().toLowerCase().contains(nombre.toLowerCase())) continue;
            }

            // Filtro por género
            if (genero != null) {
                if (p.getGenero() != genero) continue;
            }

            // Filtro por clasificación de edad
            if (edadRestriccion != null && !edadRestriccion.isBlank()) {
                if (!edadRestriccion.equalsIgnoreCase(p.getEdadRestriccion())) continue;
            }

            // Filtro por formato: requiere revisar las funciones de la cartelera
            if (formato != null) {
                boolean tieneFormato = false;
                for (Funcion f : cineObj.getCartelera().obtenerFunciones()) {
                    if (f.getPelicula() != null
                            && f.getPelicula().getId() == p.getId()
                            && f.getFormato() == formato) {
                        tieneFormato = true;
                        break;
                    }
                }
                if (!tieneFormato) continue;
            }

            resultado.add(p);
        }

        return resultado;
    }
}
