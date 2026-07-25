namespace CineFlow.Web.ViewModels.Catalogo;

public class AsientoViewModel
{
    public int IdAsiento { get; set; }
    public string Fila { get; set; } = string.Empty;
    public int Numero { get; set; }
    public string Estado { get; set; } = string.Empty;
    public bool EsConadis { get; set; }
    
    // Helper para mostrar "A1"
    public string Label => $"{Fila}{Numero}";
}
