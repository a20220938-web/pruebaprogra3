package pe.edu.pucp.cineflow.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import pe.edu.pucp.cineflow.bo.catalogo.CineBoImpl;
import pe.edu.pucp.cineflow.bo.catalogo.ICineBo;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Cine;

import java.util.List;

@WebService(
        serviceName = "CinesWS",
        targetNamespace = "http://services.cineflow.pucp.edu.pe/")
public class CinesWS {

    private final ICineBo cineBo;

    public CinesWS() {
        this.cineBo = new CineBoImpl();
    }

    @WebMethod(operationName = "listarCines")
    public List<Cine> listarCines() {
        return cineBo.listar();
    }

    @WebMethod(operationName = "obtenerCine")
    public Cine obtenerCine(@WebParam(name = "id") int id) {
        return cineBo.obtener(id);
    }

    @WebMethod(operationName = "eliminarCine")
    public void eliminarCine(@WebParam(name = "id") int id) {
        cineBo.eliminar(id);
    }

    @WebMethod(operationName = "guardarCine")
    public void guardarCine(@WebParam(name = "cine") Cine cine,
                            @WebParam(name = "estado") Estado estado) {
        cineBo.guardar(cine, estado);
    }
}
