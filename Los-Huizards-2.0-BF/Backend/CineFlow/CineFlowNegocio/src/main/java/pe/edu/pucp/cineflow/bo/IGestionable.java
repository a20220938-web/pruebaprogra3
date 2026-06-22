package pe.edu.pucp.cineflow.bo;

import pe.edu.pucp.cineflow.modelo.Estado;

import java.util.List;

public interface IGestionable<M> {
    List<M> listar();
    M obtener(int id);
    void eliminar(int id);
    void guardar(M modelo, Estado estado);
}