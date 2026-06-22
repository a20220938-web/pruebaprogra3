package pe.edu.pucp.cineflow.bo.reserva;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.reserva.ConfiteriaDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.IConfiteriaDao;
import pe.edu.pucp.cineflow.dao.reserva.IInventarioCineDao;
import pe.edu.pucp.cineflow.dao.reserva.InventarioCineDaoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reserva.InventarioCine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class InventarioCineBoImpl extends BaseBo implements IInventarioCineBo {

    private final IInventarioCineDao inventarioDao = new InventarioCineDaoImpl();
    private final IConfiteriaDao     confiteriaDao = new ConfiteriaDaoImpl();


    @Override
    public List<InventarioCine> listar() {
        return inventarioDao.leerTodos();
    }


    @Override
    public InventarioCine obtener(int id) {
        validarIdPositivo(id, "id del inventario");
        return inventarioDao.leer(id);
    }

    @Override
    public InventarioCine obtenerPorCine(int idCine) {
        validarIdPositivo(idCine, "id del cine");

        InventarioCine inventario = inventarioDao.leerPorCine(idCine);
        if (inventario == null)
            return null;

        // Enriquecer con los artículos de confitería que le pertenecen
        inventario.getMovimientos(); // lista ya inicializada en el modelo
        confiteriaDao.leerPorInventario(inventario.getIdInventario())
                .forEach(articulo -> {
                    // Los artículos quedan disponibles en la capa superior;
                    // si el modelo expone una lista de artículos, aquí se setean.
                    // Por ahora se devuelve el inventario enriquecido.
                });

        return inventario;
    }


    @Override
    public boolean verificarDisponibilidad(int idInventario, int cantidadRequerida) {
        validarIdPositivo(idInventario, "id del inventario");
        if (cantidadRequerida <= 0)
            throw new IllegalArgumentException("La cantidad requerida debe ser mayor a 0");

        InventarioCine inventario = inventarioDao.leer(idInventario);
        if (inventario == null)
            throw new IllegalStateException("No existe inventario con id: " + idInventario);

        inventario.generarAlertaStockBajo();
        return inventario.verificarDisponibilidad(cantidadRequerida);
    }


    @Override
    public void guardar(InventarioCine modelo, Estado estado) {
        Objects.requireNonNull(modelo, "El inventario no puede ser nulo");
        validarEstado(estado);

        if (modelo.getStockActual() < 0)
            throw new IllegalArgumentException("El stock actual no puede ser negativo");
        if (modelo.getStockMinimo() < 0)
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");

        if (estado == Estado.Nuevo) {
            validarIdPositivo(modelo.getIdCine(), "id del cine");
            if (modelo.getUltimaReposicion() == null)
                modelo.setUltimaReposicion(LocalDateTime.now());

            int id = inventarioDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo crear el inventario");
            modelo.setIdInventario(id);

        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getIdInventario(), "id del inventario");
            if (!inventarioDao.actualizar(modelo))
                throw new IllegalStateException(
                        "No se pudo actualizar el inventario con id: " + modelo.getIdInventario());

        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }


    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id del inventario");
        if (!inventarioDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar el inventario con id: " + id);
    }
}