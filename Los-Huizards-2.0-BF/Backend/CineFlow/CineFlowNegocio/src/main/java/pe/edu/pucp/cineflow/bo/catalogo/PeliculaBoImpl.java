package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.catalogo.FuncionDaoImpl;
import pe.edu.pucp.cineflow.dao.catalogo.IFuncionDao;
import pe.edu.pucp.cineflow.dao.catalogo.IPeliculaDao;
import pe.edu.pucp.cineflow.dao.catalogo.PeliculaDaoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;
import pe.edu.pucp.cineflow.modelo.catalogo.Genero;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PeliculaBoImpl extends BaseBo implements IPeliculaBo {

    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private final IFuncionDao funcionDao = new FuncionDaoImpl();

    @Override
    public List<Pelicula> listar() {
        pe.edu.pucp.cineflow.dao.TransactionsManager.iniciarTransaccion();
        try {
            return peliculaDao.leerTodos();
        } finally {
            pe.edu.pucp.cineflow.dao.TransactionsManager.commitTransaccion();
        }
    }

    @Override
    public Pelicula obtener(int id) {
        validarIdPositivo(id, "id");
        return peliculaDao.leer(id);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id");
        if (!peliculaDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar la película con id: " + id);
    }

    @Override
    public void guardar(Pelicula modelo, Estado estado) {
        Objects.requireNonNull(modelo, "La película no puede ser nula");
        validarTextoObligatorio(modelo.getTitulo(), "título de la película");
        validarTextoObligatorio(modelo.getSinopsis(), "sinopsis de la película");
        validarTextoObligatorio(modelo.getEdadRestriccion(), "restricción de edad");
        validarEstado(estado);

        if (modelo.getDuracion() <= 0)
            throw new IllegalArgumentException("La duración de la película debe ser mayor a 0");

        if (estado == Estado.Nuevo) {
            int id = peliculaDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo crear la película");
            modelo.setId(id);
        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getId(), "id de la película");
            if (!peliculaDao.actualizar(modelo))
                throw new IllegalStateException("No se pudo actualizar la película con id: " + modelo.getId());
        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }

    @Override
    public List<Pelicula> listarPorGenero(Genero genero) {
        return peliculaDao.leerTodos().stream()
                .filter(p -> p.getGenero() == genero)
                .collect(Collectors.toList());
    }

    @Override
    public List<Pelicula> buscar(String nombre, String cine, LocalDate fecha, Genero genero) {
        pe.edu.pucp.cineflow.dao.TransactionsManager.iniciarTransaccion();
        try {
            List<Pelicula> todas = peliculaDao.leerTodos();

            // Si hay filtro de cine o fecha, obtenemos los ids de película que tienen funciones que coincidan
            Set<Integer> idsPorFuncion = null;
            if ((cine != null && !cine.isBlank()) || fecha != null) {
                List<Funcion> funciones = fecha != null
                        ? funcionDao.leerPorFecha(fecha)
                        : funcionDao.leerTodos();

                if (cine != null && !cine.isBlank()) {
                    final String cineLower = cine.toLowerCase();
                    funciones = funciones.stream()
                            .filter(f -> f.getSala() != null
                                    && f.getSala().getCine() != null
                                    && f.getSala().getCine().getNombre() != null
                                    && f.getSala().getCine().getNombre().toLowerCase().contains(cineLower))
                            .collect(Collectors.toList());
                }

                idsPorFuncion = funciones.stream()
                        .filter(f -> f.getPelicula() != null)
                        .map(f -> f.getPelicula().getId())
                        .collect(Collectors.toSet());
            }

            final Set<Integer> idsValidos = idsPorFuncion;

            return todas.stream()
                    .filter(p -> nombre == null || nombre.isBlank()
                            || p.getTitulo().toLowerCase().contains(nombre.toLowerCase()))
                    .filter(p -> genero == null || p.getGenero() == genero)
                    .filter(p -> idsValidos == null || idsValidos.contains(p.getId()))
                    .collect(Collectors.toList());
        } finally {
            pe.edu.pucp.cineflow.dao.TransactionsManager.commitTransaccion();
        }
    }
}