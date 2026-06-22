package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.catalogo.CarteleraDaoImpl;
import pe.edu.pucp.cineflow.dao.catalogo.CineDaoImpl;
import pe.edu.pucp.cineflow.dao.catalogo.ICarteleraDao;
import pe.edu.pucp.cineflow.dao.catalogo.ICineDao;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Cartelera;
import pe.edu.pucp.cineflow.modelo.catalogo.Cine;

import java.util.List;
import java.util.Objects;

public class CineBoImpl extends BaseBo implements ICineBo {

    private final ICineDao cineDao = new CineDaoImpl();
    private final ICarteleraDao carteleraDao = new CarteleraDaoImpl();

    @Override
    public List<Cine> listar() {
        return cineDao.leerTodos();
    }

    @Override
    public Cine obtener(int id) {
        validarIdPositivo(id, "id");
        return cineDao.leer(id);
    }

    @Override
    public Cine obtenerConCartelera(int id) {
        validarIdPositivo(id, "id");
        Cine cine = cineDao.leer(id);
        if (cine == null)
            return null;

        Cartelera cartelera = carteleraDao.leerPorCine(id);
        if (cartelera != null)
            cine.setCartelera(cartelera);

        return cine;
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id");
        if (!cineDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar el cine con id: " + id);
    }

    @Override
    public void guardar(Cine modelo, Estado estado) {
        Objects.requireNonNull(modelo, "El cine no puede ser nulo");
        validarTextoObligatorio(modelo.getNombre(), "nombre del cine");
        validarTextoObligatorio(modelo.getUbicacion(), "ubicación del cine");
        validarEstado(estado);

        if (estado == Estado.Nuevo) {
            int id = cineDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo crear el cine");
            modelo.setId(id);
        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getId(), "id del cine");
            if (!cineDao.actualizar(modelo))
                throw new IllegalStateException("No se pudo actualizar el cine con id: " + modelo.getId());
        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }
}
