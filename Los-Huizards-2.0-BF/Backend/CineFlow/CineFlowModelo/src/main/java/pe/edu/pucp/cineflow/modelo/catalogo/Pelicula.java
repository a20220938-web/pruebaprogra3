package pe.edu.pucp.cineflow.modelo.catalogo;

public class Pelicula {

    private int id;
    private String titulo;
    private int duracion;
    private String sinopsis;
    private String edadRestriccion;
    private Genero genero;

    // Constructor por defecto
    public Pelicula() {}

    // Constructor por parámetros
    public Pelicula(int id, String titulo, int duracion, String sinopsis, Genero genero, String edadRestriccion) {
        this.id = id;
        this.titulo = titulo;
        this.duracion = duracion;
        this.sinopsis = sinopsis;
        this.genero = genero;
        this.edadRestriccion = edadRestriccion;
    }

    // Constructor copia
    public Pelicula(Pelicula otro) {
        this.id = otro.id;
        this.titulo = otro.titulo;
        this.duracion = otro.duracion;
        this.sinopsis = otro.sinopsis;
        this.genero = otro.genero;
        this.edadRestriccion = otro.edadRestriccion;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }

    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }

    public String getEdadRestriccion() { return edadRestriccion; }
    public void setEdadRestriccion(String edadRestriccion) { this.edadRestriccion = edadRestriccion; }

    // -----------------------------------------------------------------------
    // Métodos implementados
    // -----------------------------------------------------------------------

    /**
     * Determina si la película es apta para un espectador de la edad dada,
     * según la clasificación almacenada en edadRestriccion.
     * Clasificaciones reconocidas: "G" (todas las edades), "PG" (>= 8),
     * "PG-13" (>= 13), "R" (>= 17), "NC-17" (>= 18).
     * Si la clasificación no se reconoce se aplica criterio restrictivo (false).
     */
    public boolean esAptaPara(int edad) {
        if (edadRestriccion == null) return false;
        switch (edadRestriccion.trim().toUpperCase()) {
            case "G":     return true;
            case "PG":    return edad >= 8;
            case "PG-13": return edad >= 13;
            case "R":     return edad >= 17;
            case "NC-17": return edad >= 18;
            default:      return false;
        }
    }

    /**
     * Imprime en consola el detalle completo de la película:
     * id, título, género, duración, restricción de edad y sinopsis.
     */
    public void detalle() {
        System.out.println("===== DETALLE DE PELÍCULA =====");
        System.out.println("ID       : " + id);
        System.out.println("Título   : " + titulo);
        System.out.println("Género   : " + (genero != null ? genero : "No especificado"));
        System.out.println("Duración : " + duracion + " min");
        System.out.println("Edad mín.: " + (edadRestriccion != null ? edadRestriccion : "No especificada"));
        System.out.println("Sinopsis : " + (sinopsis != null ? sinopsis : "Sin sinopsis"));
        System.out.println("================================");
    }
}
