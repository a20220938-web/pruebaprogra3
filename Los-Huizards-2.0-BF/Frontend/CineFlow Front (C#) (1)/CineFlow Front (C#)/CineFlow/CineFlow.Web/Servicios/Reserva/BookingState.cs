using CineFlow.Web.ViewModels.Catalogo;
using CineFlow.Web.ViewModels.Reserva;

namespace CineFlow.Web.Servicios.Reserva;

public class BookingState
{
    public FuncionViewModel? CurrentFuncion { get; set; }
    public List<int> SelectedSeats { get; set; } = new();
    public Dictionary<int, string> TicketTypes { get; set; } = new(); // seatId -> type (REGULAR, NIÑO, CONADIS)
    public Dictionary<int, int> SelectedSnacks { get; set; } = new(); // snackId -> quantity
    public int IdReserva { get; set; }

    // Cache the loaded snacks
    public List<ConfiteriaViewModel> AvailableSnacks { get; set; } = new();

    public void Clear()
    {
        CurrentFuncion = null;
        SelectedSeats.Clear();
        TicketTypes.Clear();
        SelectedSnacks.Clear();
        IdReserva = 0;
    }
}
