using CineFlow.Web.ViewModels.Catalogo;

namespace CineFlow.Web.ViewModels.Catalogo;

public class ReporteVentasPeliculaViewModel
{
    public int IdReporte { get; set; }
    public int CantidadEntradasVendidas { get; set; }
    public double MontoTotalRecaudado { get; set; }
    public PeliculaViewModel? Pelicula { get; set; }
}
