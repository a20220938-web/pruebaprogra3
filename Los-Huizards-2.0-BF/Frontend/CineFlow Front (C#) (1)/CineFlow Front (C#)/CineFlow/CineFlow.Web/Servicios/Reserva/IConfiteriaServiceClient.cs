using CineFlow.Web.ViewModels.Reserva;

namespace CineFlow.Web.Servicios.Reserva;

public interface IConfiteriaServiceClient
{
    Task<List<ConfiteriaViewModel>> ListarAsync();
    Task<ConfiteriaViewModel?> CrearAsync(ConfiteriaViewModel item);
    Task<bool> ActualizarAsync(int id, ConfiteriaViewModel item);
    Task<bool> EliminarAsync(int id);
}
