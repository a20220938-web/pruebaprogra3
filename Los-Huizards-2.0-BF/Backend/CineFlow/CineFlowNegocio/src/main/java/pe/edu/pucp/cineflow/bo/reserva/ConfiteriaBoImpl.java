package pe.edu.pucp.cineflow.bo.reserva;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.reserva.ConfiteriaDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.IConfiteriaDao;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reserva.ArticuloIndividual;

import java.util.List;
import java.util.Objects;

public class ConfiteriaBoImpl extends BaseBo implements IConfiteriaBo {

    private final IConfiteriaDao confiteriaDao = new ConfiteriaDaoImpl();


    @Override
    public List<ArticuloIndividual> listar() {
        return confiteriaDao.leerTodos();
    }

    @Override
    public List<ArticuloIndividual> listarPorInventario(int idInventario) {
        validarIdPositivo(idInventario, "id de inventario");
        return confiteriaDao.leerPorInventario(idInventario);
    }


    @Override
    public ArticuloIndividual obtener(int id) {
        validarIdPositivo(id, "id del artículo");
        return confiteriaDao.leer(id);
    }


    @Override
    public void guardar(ArticuloIndividual modelo, Estado estado) {
        Objects.requireNonNull(modelo, "El artículo no puede ser nulo");
        validarEstado(estado);
        validarTextoObligatorio(modelo.getNombre(), "nombre del artículo");
        validarTextoObligatorio(modelo.getCategoria(), "categoría del artículo");
        validarMontoPositivo(modelo.getPrecioUnitario(), "precio unitario");

        if (modelo.getCantidad() < 0)
            throw new IllegalArgumentException("La cantidad no puede ser negativa");

        if (estado == Estado.Nuevo) {
            validarIdPositivo(modelo.getIdInventario(), "id de inventario");
            int id = confiteriaDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo crear el artículo de confitería");
            modelo.setIdItem(id);

        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getIdItem(), "id del artículo");
            if (!confiteriaDao.actualizar(modelo))
                throw new IllegalStateException(
                        "No se pudo actualizar el artículo con id: " + modelo.getIdItem());

        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }


    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id del artículo");
        if (!confiteriaDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar el artículo con id: " + id);
    }
}