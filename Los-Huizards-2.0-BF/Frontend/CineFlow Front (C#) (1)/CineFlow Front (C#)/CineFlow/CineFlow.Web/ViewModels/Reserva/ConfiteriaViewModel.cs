namespace CineFlow.Web.ViewModels.Reserva;

public class ConfiteriaViewModel
{
    public int IdItem { get; set; }
    public string Nombre { get; set; } = string.Empty;
    public string Descripcion { get; set; } = string.Empty;
    public int Cantidad { get; set; }
    public double PrecioUnitario { get; set; }
    public string Categoria { get; set; } = string.Empty;
    public int IdInventario { get; set; }
}
