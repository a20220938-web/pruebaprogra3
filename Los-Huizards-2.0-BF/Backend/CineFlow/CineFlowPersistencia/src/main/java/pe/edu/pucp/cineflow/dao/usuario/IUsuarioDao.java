package pe.edu.pucp.cineflow.dao.usuario;

import pe.edu.pucp.cineflow.dao.IPersistible;
import pe.edu.pucp.cineflow.modelo.usuario.Usuario;

public interface IUsuarioDao extends IPersistible<Usuario, Integer> {
    Usuario leerPorEmail(String email);
}
