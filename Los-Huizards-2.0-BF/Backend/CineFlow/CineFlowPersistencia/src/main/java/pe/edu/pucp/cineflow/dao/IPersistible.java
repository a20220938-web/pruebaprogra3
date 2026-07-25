package pe.edu.pucp.cineflow.dao;

import java.util.List;


public interface IPersistible<M, I> {
    I crear(M modelo);
    boolean actualizar(M modelo);
    boolean eliminar(I id);
    M leer(I id);
    List<M> leerTodos();
}
