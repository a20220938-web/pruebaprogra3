using CineFlow.Web.ViewModels.Reserva;

namespace CineFlow.Web.Servicios.Reserva;

public interface IInventarioServiceClient
{
    Task<List<InventarioViewModel>> ListarAsync();
    Task<bool> ActualizarAsync(int id, InventarioViewModel inventario);
}
