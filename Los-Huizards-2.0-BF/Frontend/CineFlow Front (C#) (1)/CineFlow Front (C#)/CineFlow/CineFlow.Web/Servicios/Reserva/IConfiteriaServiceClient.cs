using CineFlow.Web.ViewModels.Reserva;

namespace CineFlow.Web.Servicios.Reserva;

public interface IConfiteriaServiceClient
{
    Task<List<ConfiteriaViewModel>> ListarAsync();
    Task<List<ConfiteriaViewModel>> ListarPorCineAsync(int idCine);
    Task<ConfiteriaViewModel?> CrearAsync(ConfiteriaViewModel item);
    Task<bool> ActualizarAsync(int id, ConfiteriaViewModel item);
    Task<bool> EliminarAsync(int id);
    Task<bool> DescontarStockAsync(int id, int cantidad);
    Task<bool> RestaurarStockAsync(int id, int cantidad);
}
