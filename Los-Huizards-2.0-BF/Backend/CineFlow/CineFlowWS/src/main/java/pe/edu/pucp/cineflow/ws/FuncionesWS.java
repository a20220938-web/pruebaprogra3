package pe.edu.pucp.cineflow.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import pe.edu.pucp.cineflow.bo.catalogo.FuncionBoImpl;
import pe.edu.pucp.cineflow.bo.catalogo.IFuncionBo;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;

import java.time.LocalDateTime;
import java.util.List;

@WebService(
        serviceName = "FuncionesWS",
        targetNamespace = "http://services.cineflow.pucp.edu.pe/")
public class FuncionesWS {

    private final IFuncionBo funcionBo;

    public FuncionesWS() {
        this.funcionBo = new FuncionBoImpl();
    }

    @WebMethod(operationName = "listarFunciones")
    public List<Funcion> listarFunciones() {
        return funcionBo.listar();
    }

    @WebMethod(operationName = "obtenerFuncion")
    public Funcion obtenerFuncion(@WebParam(name = "id") int id) {
        return funcionBo.obtener(id);
    }

    @WebMethod(operationName = "eliminarFuncion")
    public void eliminarFuncion(@WebParam(name = "id") int id) {
        funcionBo.eliminar(id);
    }

    @WebMethod(operationName = "guardarFuncion")
    public void guardarFuncion(@WebParam(name = "funcion") Funcion funcion,
                               @WebParam(name = "estado") Estado estado) {
        funcionBo.guardar(funcion, estado);
    }

    @WebMethod(operationName = "listarFuncionesPorPelicula")
    public List<Funcion> listarFuncionesPorPelicula(@WebParam(name = "idPelicula") int idPelicula) {
        return funcionBo.listarPorPelicula(idPelicula);
    }

    @WebMethod(operationName = "listarFuncionesPorFecha")
    public List<Funcion> listarFuncionesPorFecha(@WebParam(name = "fecha") LocalDateTime fecha) {
        return funcionBo.listarPorFecha(fecha);
    }
}
