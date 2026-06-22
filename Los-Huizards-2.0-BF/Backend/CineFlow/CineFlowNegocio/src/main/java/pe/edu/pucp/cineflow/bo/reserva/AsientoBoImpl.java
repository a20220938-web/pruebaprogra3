package pe.edu.pucp.cineflow.bo.reserva;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.reserva.AsientoDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.IAsientoDao;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;

import java.util.List;
import java.util.Objects;

public class AsientoBoImpl extends BaseBo implements IAsientoBo {

    private final IAsientoDao asientoDao = new AsientoDaoImpl();

    @Override
    public List<Asiento> listar() {
        return asientoDao.leerTodos();
    }

    @Override
    public List<Asiento> listarPorFuncion(int idFuncion) {
        validarIdPositivo(idFuncion, "id de función");
        return asientoDao.leerPorFuncion(idFuncion);
    }

    @Override
    public Asiento obtener(int id) {
        validarIdPositivo(id, "id de asiento");
        return asientoDao.leer(id);
    }

    // RF08 - Cambiar estado del asiento (DISPONIBLE → RESERVADO → OCUPADO / liberar)
    @Override
    public void cambiarEstado(int idAsiento, EstadoAsiento nuevoEstado) {
        validarIdPositivo(idAsiento, "id de asiento");
        Objects.requireNonNull(nuevoEstado, "El nuevo estado no puede ser nulo");

        Asiento asiento = asientoDao.leer(idAsiento);
        if (asiento == null)
            throw new IllegalStateException("No existe el asiento con id: " + idAsiento);

        // Validar transiciones permitidas (máquina de estados RF08)
        EstadoAsiento actual = asiento.getEstado();
        boolean transicionValida = switch (actual) {
            case DISPONIBLE -> nuevoEstado == EstadoAsiento.RESERVADO;
            case RESERVADO  -> nuevoEstado == EstadoAsiento.DISPONIBLE || nuevoEstado == EstadoAsiento.OCUPADO;
            case OCUPADO    -> false; // estado final
        };

        if (!transicionValida)
            throw new IllegalStateException(
                    "Transición no permitida: " + actual + " → " + nuevoEstado);

        asiento.setEstado(nuevoEstado);
        if (!asientoDao.actualizar(asiento))
            throw new IllegalStateException("No se pudo actualizar el estado del asiento");
    }

    @Override
    public void guardar(Asiento modelo, Estado estado) {
        Objects.requireNonNull(modelo, "El asiento no puede ser nulo");
        validarEstado(estado);

        if (estado == Estado.Nuevo) {
            int id = asientoDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo crear el asiento");
            modelo.setIdAsiento(id);
        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getIdAsiento(), "id de asiento");
            if (!asientoDao.actualizar(modelo))
                throw new IllegalStateException("No se pudo actualizar el asiento con id: " + modelo.getIdAsiento());
        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id de asiento");
        if (!asientoDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar el asiento con id: " + id);
    }
}
