namespace CineFlow.Web.ViewModels.Reserva;

// Refleja la tabla inventario_cine (stock por cine).
public class InventarioViewModel
{
    public int IdInventario { get; set; }
    public int StockActual { get; set; }
    public int StockMinimo { get; set; }
    public string? UltimaReposicion { get; set; } // ISO local (yyyy-MM-ddTHH:mm:ss)
    public int IdCine { get; set; }
}
