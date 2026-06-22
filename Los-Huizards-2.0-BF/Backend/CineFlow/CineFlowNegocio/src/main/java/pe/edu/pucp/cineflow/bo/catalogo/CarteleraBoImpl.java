package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.catalogo.CarteleraDaoImpl;
import pe.edu.pucp.cineflow.dao.catalogo.ICarteleraDao;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Cartelera;
import pe.edu.pucp.cineflow.modelo.catalogo.FiltroBusqueda;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CarteleraBoImpl extends BaseBo implements ICarteleraBo {

    private final ICarteleraDao carteleraDao = new CarteleraDaoImpl();

    @Override
    public List<Cartelera> listar() {
        return carteleraDao.leerTodos();
    }

    @Override
    public Cartelera obtener(int id) {
        validarIdPositivo(id, "id");
        return carteleraDao.leer(id);
    }

    @Override
    public Cartelera obtenerPorCine(int idCine) {
        validarIdPositivo(idCine, "id del cine");
        return carteleraDao.leerPorCine(idCine);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id");
        if (!carteleraDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar la cartelera con id: " + id);
    }

    @Override
    public void guardar(Cartelera modelo, Estado estado) {
        Objects.requireNonNull(modelo, "La cartelera no puede ser nula");
        validarEstado(estado);

        if (estado == Estado.Nuevo) {
            int id = carteleraDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo crear la cartelera");
            modelo.setId(id);
        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getId(), "id de la cartelera");
            if (!carteleraDao.actualizar(modelo))
                throw new IllegalStateException("No se pudo actualizar la cartelera con id: " + modelo.getId());
        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }

    @Override
    public List<Pelicula> buscarPeliculas(FiltroBusqueda filtro) {
        if (filtro == null)
            throw new IllegalArgumentException("El filtro no puede ser nulo");

        List<Pelicula> peliculas = carteleraDao.leerTodos().stream()
                .flatMap(c -> c.getPeliculas().stream())
                .distinct()
                .collect(Collectors.toList());

        if (filtro.getNombre() != null && !filtro.getNombre().isBlank())
            peliculas = peliculas.stream()
                    .filter(p -> p.getTitulo().toLowerCase()
                            .contains(filtro.getNombre().toLowerCase()))
                    .collect(Collectors.toList());

        if (filtro.getEdadRestriccion() != null && !filtro.getEdadRestriccion().isBlank())
            peliculas = peliculas.stream()
                    .filter(p -> filtro.getEdadRestriccion().equals(p.getEdadRestriccion()))
                    .collect(Collectors.toList());

        return peliculas;
    }
}