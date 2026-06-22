package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.catalogo.FuncionDaoImpl;
import pe.edu.pucp.cineflow.dao.catalogo.IFuncionDao;
import pe.edu.pucp.cineflow.dao.reserva.AsientoDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.IAsientoDao;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class FuncionBoImpl extends BaseBo implements IFuncionBo {

    private final IFuncionDao funcionDao = new FuncionDaoImpl();

    @Override
    public List<Funcion> listar() {
        return funcionDao.leerTodos();
    }

    @Override
    public Funcion obtener(int id) {
        validarIdPositivo(id, "id");
        Funcion f = funcionDao.leer(id);
        if (f != null) {
            IAsientoDao asientoDao = new AsientoDaoImpl();
            List<Asiento> asientosReales = asientoDao.leerPorFuncion(id);
            if (asientosReales != null && !asientosReales.isEmpty()) {
                f.getMapaAsientos().clear();
                f.getMapaAsientos().addAll(asientosReales);
            }
        }
        return f;
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id");
        if (!funcionDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar la función con id: " + id);
    }

    @Override
    public void guardar(Funcion modelo, Estado estado) {
        Objects.requireNonNull(modelo, "La función no puede ser nula");
        Objects.requireNonNull(modelo.getPelicula(), "La película de la función es obligatoria");
        validarIdPositivo(modelo.getPelicula().getId(), "id de la película");
        Objects.requireNonNull(modelo.getSala(), "La sala de la función es obligatoria");
        validarIdPositivo(modelo.getSala().getId(), "id de la sala");
        validarMontoPositivo(modelo.getPrecioBase(), "precio base");
        Objects.requireNonNull(modelo.getFechaHora(), "La fecha y hora de la función es obligatoria");
        validarEstado(estado);

        if (estado == Estado.Nuevo) {
            if (modelo.getFechaHora().isBefore(LocalDateTime.now()))
                throw new IllegalArgumentException("No se puede registrar una función con fecha y hora en el pasado");

            int id = funcionDao.crear(modelo);
            if (id <= 0) throw new IllegalStateException("No se pudo crear la función");
            modelo.setId(id);

            // Persistir asientos con IDs reales de BD
            IAsientoDao asientoDao = new AsientoDaoImpl();
            // Primero generamos los asientos basandonos en la configuracion de la sala
            modelo.generarMapaAsientos();
            // Tomamos los asientos generados por la logica interna de Funcion (con las letras y numeros correctos)
            List<Asiento> asientosGenerados = new java.util.ArrayList<>(modelo.getMapaAsientos());
            modelo.getMapaAsientos().clear();
            
            for (Asiento a : asientosGenerados) {
                // Asegurarnos de que estan disponibles
                a.setEstado(EstadoAsiento.DISPONIBLE);
                int idAsiento = asientoDao.crear(a);
                a.setIdAsiento(idAsiento);
                modelo.getMapaAsientos().add(a);
            }
        }else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getId(), "id de la función");
            if (!funcionDao.actualizar(modelo))
                throw new IllegalStateException(
                        "No se pudo actualizar la función con id: " + modelo.getId());
        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }

    @Override
    public List<Funcion> listarPorPelicula(int idPelicula) {
        validarIdPositivo(idPelicula, "id de la película");
        return funcionDao.leerPorPelicula(idPelicula);
    }

    @Override
    public List<Funcion> listarPorFecha(LocalDateTime fecha) {
        if (fecha == null)
            throw new IllegalArgumentException("La fecha no puede ser nula");
        // IFuncionDao recibe LocalDate — extraemos solo la fecha del LocalDateTime
        return funcionDao.leerPorFecha(fecha.toLocalDate());
    }
}