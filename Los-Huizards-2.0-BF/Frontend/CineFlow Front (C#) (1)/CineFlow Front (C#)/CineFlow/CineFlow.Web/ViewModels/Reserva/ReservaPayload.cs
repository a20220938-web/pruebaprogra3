namespace CineFlow.Web.ViewModels.Reserva;

public class ReservaPayload
{
    public int IdUsuario { get; set; }
    public int IdFuncion { get; set; }
    // Asiento ID -> Tipo Entrada
    public Dictionary<string, string> TiposEntrada { get; set; } = new();
    // Confiteria ID -> Cantidad
    public Dictionary<int, int> Snacks { get; set; } = new();
}
