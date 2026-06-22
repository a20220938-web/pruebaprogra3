package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.catalogo.ISalaDao;
import pe.edu.pucp.cineflow.dao.catalogo.SalaDaoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Sala;

import java.util.List;
import java.util.Objects;

public class SalaBoImpl extends BaseBo implements ISalaBo {

    private final ISalaDao salaDao = new SalaDaoImpl();

    @Override
    public List<Sala> listar() {
        return salaDao.leerTodos();
    }

    @Override
    public Sala obtener(int id) {
        validarIdPositivo(id, "id");
        return salaDao.leer(id);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id");
        if (!salaDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar la sala con id: " + id);
    }

    @Override
    public void guardar(Sala modelo, Estado estado) {
        Objects.requireNonNull(modelo, "La sala no puede ser nula");
        validarEstado(estado);

        if (modelo.getNumero() <= 0)
            throw new IllegalArgumentException("El número de sala debe ser mayor a 0");
        if (modelo.getCapacidad() <= 0)
            throw new IllegalArgumentException("La capacidad de la sala debe ser mayor a 0");
        Objects.requireNonNull(modelo.getCine(), "El cine de la sala es obligatorio");
        validarIdPositivo(modelo.getCine().getId(), "id del cine");

        if (estado == Estado.Nuevo) {
            int id = salaDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo crear la sala");
            modelo.setId(id);
        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getId(), "id de la sala");
            if (!salaDao.actualizar(modelo))
                throw new IllegalStateException("No se pudo actualizar la sala con id: " + modelo.getId());
        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }
}