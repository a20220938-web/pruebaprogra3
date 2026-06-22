package pe.edu.pucp.cineflow.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import pe.edu.pucp.cineflow.bo.catalogo.IPeliculaBo;
import pe.edu.pucp.cineflow.bo.catalogo.PeliculaBoImpl;
import pe.edu.pucp.cineflow.modelo.Estado;
import pe.edu.pucp.cineflow.modelo.catalogo.Genero;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;

import java.util.List;

@WebService(
        serviceName = "PeliculasWS",
        targetNamespace = "http://services.cineflow.pucp.edu.pe/")
public class PeliculasWS {

    private final IPeliculaBo peliculaBo;

    public PeliculasWS() {
        this.peliculaBo = new PeliculaBoImpl();
    }

    @WebMethod(operationName = "listarPeliculas")
    public List<Pelicula> listarPeliculas() {
        return peliculaBo.listar();
    }

    @WebMethod(operationName = "obtenerPelicula")
    public Pelicula obtenerPelicula(@WebParam(name = "id") int id) {
        return peliculaBo.obtener(id);
    }

    @WebMethod(operationName = "eliminarPelicula")
    public void eliminarPelicula(@WebParam(name = "id") int id) {
        peliculaBo.eliminar(id);
    }

    @WebMethod(operationName = "guardarPelicula")
    public void guardarPelicula(@WebParam(name = "pelicula") Pelicula pelicula,
                                @WebParam(name = "estado") Estado estado) {
        peliculaBo.guardar(pelicula, estado);
    }

    @WebMethod(operationName = "listarPeliculasPorGenero")
    public List<Pelicula> listarPeliculasPorGenero(@WebParam(name = "genero") Genero genero) {
        return peliculaBo.listarPorGenero(genero);
    }
}
