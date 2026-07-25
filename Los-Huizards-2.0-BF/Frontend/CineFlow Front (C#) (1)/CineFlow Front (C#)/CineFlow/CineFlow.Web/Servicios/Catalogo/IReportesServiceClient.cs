using CineFlow.Web.ViewModels.Catalogo;

namespace CineFlow.Web.Servicios.Catalogo;

public interface IReportesServiceClient
{
    Task<ReporteVentasPeliculaViewModel?> ObtenerVentasPorPeliculaAsync(int idPelicula, DateTime? fechaInicio = null, DateTime? fechaFin = null);
}
