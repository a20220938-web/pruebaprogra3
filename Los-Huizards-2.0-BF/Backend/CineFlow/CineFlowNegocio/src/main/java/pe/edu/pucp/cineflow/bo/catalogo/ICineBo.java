package pe.edu.pucp.cineflow.bo.catalogo;

import pe.edu.pucp.cineflow.bo.IGestionable;
import pe.edu.pucp.cineflow.modelo.catalogo.Cine;

public interface ICineBo extends IGestionable<Cine> {
    Cine obtenerConCartelera(int id);
}