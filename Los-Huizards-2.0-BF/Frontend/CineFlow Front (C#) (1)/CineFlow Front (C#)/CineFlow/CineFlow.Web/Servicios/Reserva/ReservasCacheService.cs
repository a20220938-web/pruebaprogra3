using CineFlow.Web.ViewModels.Reserva;

namespace CineFlow.Web.Servicios.Reserva;

public class ReservasCacheService
{
    private List<ReservaViewModel>? _cached;
    private DateTime _lastFetch = DateTime.MinValue;
    private static readonly TimeSpan StaleThreshold = TimeSpan.FromSeconds(30);

    public bool HasCachedData => _cached != null;

    public List<ReservaViewModel>? GetCached() => _cached;

    public bool IsStale => !HasCachedData || (DateTime.Now - _lastFetch) > StaleThreshold;

    public void Set(List<ReservaViewModel> reservas)
    {
        _cached = reservas;
        _lastFetch = DateTime.Now;
    }

    public void UpdateReserva(int idReserva, Action<ReservaViewModel> update)
    {
        var r = _cached?.FirstOrDefault(x => x.IdReserva == idReserva);
        if (r != null) update(r);
    }

    public void Invalidate()
    {
        _cached = null;
        _lastFetch = DateTime.MinValue;
    }
}
