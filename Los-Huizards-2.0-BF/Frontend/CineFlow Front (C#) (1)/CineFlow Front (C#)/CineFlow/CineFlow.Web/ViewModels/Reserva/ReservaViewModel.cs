namespace CineFlow.Web.ViewModels.Reserva;

public class ReservaViewModel
{
    public int IdReserva { get; set; }
    public DateTime FechaReserva { get; set; }

    [System.Text.Json.Serialization.JsonIgnore(Condition = System.Text.Json.Serialization.JsonIgnoreCondition.WhenWritingNull)]
    public string? Estado { get; set; }

    public double TotalFinal { get; set; }

    public UsuarioReservaViewModel Usuario { get; set; } = new();
    public FuncionReservaViewModel Funcion { get; set; } = new();

    [System.Text.Json.Serialization.JsonIgnore(Condition = System.Text.Json.Serialization.JsonIgnoreCondition.WhenWritingNull)]
    public PagoReservaViewModel? Pago { get; set; }

    public List<EntradaViewModel> Entradas { get; set; } = new();
    public List<AsientoReservaViewModel> Asientos { get; set; } = new();
}

public class PagoReservaViewModel
{
    public int IdPago { get; set; }
    public double Monto { get; set; }
    public string Metodo { get; set; } = string.Empty;

    [System.Text.Json.Serialization.JsonIgnore(Condition = System.Text.Json.Serialization.JsonIgnoreCondition.WhenWritingNull)]
    public string? Estado { get; set; }
}

public class UsuarioReservaViewModel
{
    public int IdUsuario { get; set; }
    public string? Nombre { get; set; }
}

public class FuncionReservaViewModel
{
    public int Id { get; set; }
    // Only used for display in history
    public CineFlow.Web.ViewModels.Catalogo.PeliculaViewModel? Pelicula { get; set; }
    public CineFlow.Web.ViewModels.Catalogo.SalaViewModel? Sala { get; set; }
    public DateTime FechaHora { get; set; }
}

public class EntradaViewModel
{
    public double PrecioBase { get; set; }
    public string Tipo { get; set; } = string.Empty;
}

public class AsientoReservaViewModel
{
    public int IdAsiento { get; set; }
    public string Fila { get; set; } = string.Empty;
    public int Numero { get; set; }
    
    [System.Text.Json.Serialization.JsonIgnore(Condition = System.Text.Json.Serialization.JsonIgnoreCondition.WhenWritingNull)]
    public string? Estado { get; set; }
}
