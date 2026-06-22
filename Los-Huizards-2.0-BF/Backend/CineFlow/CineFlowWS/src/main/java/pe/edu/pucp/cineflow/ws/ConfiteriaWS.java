package pe.edu.pucp.cineflow.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import pe.edu.pucp.cineflow.bo.reserva.ConfiteriaBoImpl;
import pe.edu.pucp.cineflow.bo.reserva.IConfiteriaBo;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.reserva.ArticuloIndividual;

import java.util.List;

@WebService(
        serviceName = "ConfiteriaWS",
        targetNamespace = "http://services.cineflow.pucp.edu.pe/")
public class ConfiteriaWS {

    private final IConfiteriaBo confiteriaBo;

    public ConfiteriaWS() {
        this.confiteriaBo = new ConfiteriaBoImpl();
    }

    @WebMethod(operationName = "listarArticulos")
    public List<ArticuloIndividual> listarArticulos() {
        return confiteriaBo.listar();
    }

    @WebMethod(operationName = "obtenerArticulo")
    public ArticuloIndividual obtenerArticulo(@WebParam(name = "id") int id) {
        return confiteriaBo.obtener(id);
    }

    @WebMethod(operationName = "eliminarArticulo")
    public void eliminarArticulo(@WebParam(name = "id") int id) {
        confiteriaBo.eliminar(id);
    }

    @WebMethod(operationName = "guardarArticulo")
    public void guardarArticulo(@WebParam(name = "articulo") ArticuloIndividual articulo,
                                @WebParam(name = "estado") Estado estado) {
        confiteriaBo.guardar(articulo, estado);
    }

    @WebMethod(operationName = "listarArticulosPorInventario")
    public List<ArticuloIndividual> listarArticulosPorInventario(
            @WebParam(name = "idInventario") int idInventario) {
        return confiteriaBo.listarPorInventario(idInventario);
    }
}
