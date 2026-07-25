package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.catalogo.FuncionDaoImpl;
import pe.edu.pucp.cineflow.dao.catalogo.IFuncionDao;
import pe.edu.pucp.cineflow.dao.catalogo.PeliculaDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.AsientoDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.IAsientoDao;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;
import pe.edu.pucp.cineflow.modelo.reserva.Asiento;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoAsiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class FuncionBoImpl extends BaseBo implements IFuncionBo {

    private final IFuncionDao funcionDao = new FuncionDaoImpl();

    @Override
    public List<Funcion> listar() {
        pe.edu.pucp.cineflow.dao.TransactionsManager.iniciarTransaccion();
        try {
            return funcionDao.leerTodos();
        } finally {
            pe.edu.pucp.cineflow.dao.TransactionsManager.commitTransaccion();
        }
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

        pe.edu.pucp.cineflow.dao.TransactionsManager.iniciarTransaccion();
        try {
            if (estado == Estado.Nuevo) {
                if (modelo.getFechaHora().isBefore(LocalDateTime.now()))
                    throw new IllegalArgumentException("No se puede registrar una función con fecha y hora en el pasado");

                validarSinCruceDeHorario(modelo, null);

                int id = funcionDao.crear(modelo);
                if (id <= 0) throw new IllegalStateException("No se pudo crear la función");
                modelo.setId(id);

                // Persistir asientos con IDs reales de BD
                IAsientoDao asientoDao = new AsientoDaoImpl();
                // Hidratar la sala completa desde la base de datos para obtener filas y columnas
                pe.edu.pucp.cineflow.dao.catalogo.ISalaDao salaDao = new pe.edu.pucp.cineflow.dao.catalogo.SalaDaoImpl();
                modelo.setSala(salaDao.leer(modelo.getSala().getId()));
                
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
                validarSinCruceDeHorario(modelo, modelo.getId());
                if (!funcionDao.actualizar(modelo))
                    throw new IllegalStateException(
                            "No se pudo actualizar la función con id: " + modelo.getId());
            } else {
                throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
            }
            pe.edu.pucp.cineflow.dao.TransactionsManager.commitTransaccion();
        } catch (Exception e) {
            pe.edu.pucp.cineflow.dao.TransactionsManager.rollbackTransaccion();
            throw e;
        }
    }

    private static final int MINUTOS_LIMPIEZA_SALA = 15;

    /** Verifica que la función no se cruce en horario con otra función ya registrada en la misma sala. */
    private void validarSinCruceDeHorario(Funcion modelo, Integer idAExcluir) {
        Pelicula pelicula = new PeliculaDaoImpl().leer(modelo.getPelicula().getId());
        int duracion = (pelicula != null) ? pelicula.getDuracion() : 0;

        LocalDateTime nuevoInicio = modelo.getFechaHora();
        LocalDateTime nuevoFin = nuevoInicio.plusMinutes(duracion + MINUTOS_LIMPIEZA_SALA);

        List<Funcion> funcionesDeLaSala = funcionDao.leerPorSala(modelo.getSala().getId());
        for (Funcion existente : funcionesDeLaSala) {
            if (idAExcluir != null && existente.getId() == idAExcluir) continue;

            int duracionExistente = (existente.getPelicula() != null) ? existente.getPelicula().getDuracion() : 0;
            LocalDateTime existenteInicio = existente.getFechaHora();
            LocalDateTime existenteFin = existenteInicio.plusMinutes(duracionExistente + MINUTOS_LIMPIEZA_SALA);

            boolean seCruzan = nuevoInicio.isBefore(existenteFin) && existenteInicio.isBefore(nuevoFin);
            if (seCruzan) {
                throw new IllegalArgumentException(
                        "Ya existe una función en esta sala que se cruza con el horario indicado (función #"
                                + existente.getId() + ", " + existenteInicio + ")");
            }
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