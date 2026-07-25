namespace CineFlow.Web.ViewModels.Catalogo;

public class SalaViewModel
{
    public int Id { get; set; }
    public int Numero { get; set; }
    public int Capacidad { get; set; }
    public int Filas { get; set; }
    public int ColumnasPorFila { get; set; }
    public CineViewModel? Cine { get; set; }
}
