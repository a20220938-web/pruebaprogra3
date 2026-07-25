package pe.edu.pucp.cineflow.bo.reporte;

import pe.edu.pucp.cineflow.bo.BaseBo;
import pe.edu.pucp.cineflow.dao.reporte.ComprobanteCompraDaoImpl;
import pe.edu.pucp.cineflow.dao.reporte.IComprobanteCompraDao;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reporte.ComprobanteCompra;

import java.util.List;

public class ComprobanteBoImpl extends BaseBo implements IComprobanteBo {

    private final IComprobanteCompraDao comprobanteDao = new ComprobanteCompraDaoImpl();

    @Override
    public List<ComprobanteCompra> listar() {
        return comprobanteDao.leerTodos();
    }

    @Override
    public ComprobanteCompra obtener(int id) {
        validarIdPositivo(id, "id de comprobante");
        return comprobanteDao.leer(id);
    }

    @Override
    public ComprobanteCompra obtenerPorReserva(int idReserva) {
        validarIdPositivo(idReserva, "id de reserva");
        return comprobanteDao.leerPorReserva(idReserva);
    }

    @Override
    public void eliminar(int id) {
        validarIdPositivo(id, "id de comprobante");
        if (!comprobanteDao.eliminar(id))
            throw new IllegalStateException("No se pudo eliminar el comprobante con id: " + id);
    }

    @Override
    public void guardar(ComprobanteCompra modelo, Estado estado) {
        validarComprobante(modelo);
        validarEstado(estado);

        if (estado == Estado.Nuevo) {
            int id = comprobanteDao.crear(modelo);
            if (id <= 0)
                throw new IllegalStateException("No se pudo crear el comprobante");
            modelo.setIdComprobante(id);
        } else if (estado == Estado.Modificado) {
            validarIdPositivo(modelo.getIdComprobante(), "id de comprobante");
            if (!comprobanteDao.actualizar(modelo))
                throw new IllegalStateException(
                        "No se pudo actualizar el comprobante con id: " + modelo.getIdComprobante());
        } else {
            throw new IllegalArgumentException("Estado no soportado en guardar: " + estado);
        }
    }


    private void validarComprobante(ComprobanteCompra modelo) {
        if (modelo == null)
            throw new IllegalArgumentException("El comprobante no puede ser nulo");
        if (modelo.getReserva() == null)
            throw new IllegalArgumentException("La reserva es obligatoria");
        validarIdPositivo(modelo.getReserva().getIdReserva(), "id de reserva");
        validarMontoPositivo(modelo.getMontoTotal(), "monto total");
    }
}