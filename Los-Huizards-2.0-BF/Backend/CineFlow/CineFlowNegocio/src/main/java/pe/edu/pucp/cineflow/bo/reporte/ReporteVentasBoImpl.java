package pe.edu.pucp.cineflow.bo.reporte;

import pe.edu.pucp.cineflow.dao.catalogo.FuncionDaoImpl;
import pe.edu.pucp.cineflow.dao.catalogo.IFuncionDao;
import pe.edu.pucp.cineflow.dao.catalogo.IPeliculaDao;
import pe.edu.pucp.cineflow.dao.catalogo.PeliculaDaoImpl;
import pe.edu.pucp.cineflow.dao.reserva.IReservaDao;
import pe.edu.pucp.cineflow.dao.reserva.ReservaDaoImpl;
import pe.edu.pucp.cineflow.modelo.catalogo.Funcion;
import pe.edu.pucp.cineflow.modelo.catalogo.Pelicula;
import pe.edu.pucp.cineflow.modelo.reporte.ReporteVentasPorPelicula;
import pe.edu.pucp.cineflow.modelo.reserva.EstadoReserva;
import pe.edu.pucp.cineflow.modelo.reserva.Reserva;

import java.util.List;
import java.util.stream.Collectors;

public class ReporteVentasBoImpl implements IReporteVentasBo {

    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private final IFuncionDao  funcionDao  = new FuncionDaoImpl();
    private final IReservaDao  reservaDao  = new ReservaDaoImpl();

    // RF17 - Reporte de ventas por película (con filtro opcional de cine)
    @Override
    public ReporteVentasPorPelicula generarPorPelicula(int idPelicula, Integer idCine) {
        Pelicula pelicula = peliculaDao.leer(idPelicula);
        if (pelicula == null)
            throw new IllegalArgumentException("No existe la película con id: " + idPelicula);

        // Obtener funciones de esa película (filtradas por cine si se indica)
        List<Funcion> funciones = funcionDao.leerPorPelicula(idPelicula);
        if (idCine != null) {
            funciones = funciones.stream()
                    .filter(f -> f.getSala() != null
                            && f.getSala().getCine() != null
                            && f.getSala().getCine().getId() == idCine)
                    .collect(Collectors.toList());
        }

        // Recopilar reservas CONFIRMADAS de esas funciones
        ReporteVentasPorPelicula reporte = new ReporteVentasPorPelicula();
        reporte.setPelicula(pelicula);

        for (Funcion funcion : funciones) {
            List<Reserva> reservasFuncion = reservaDao.leerPorFuncion(funcion.getId());
            reservasFuncion.stream()
                    .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                    .forEach(reporte::agregarReserva);
        }

        return reporte;
    }
}
