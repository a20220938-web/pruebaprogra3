using CineFlow.Web.ViewModels.Catalogo;

namespace CineFlow.Web.Servicios.Catalogo;

public interface ICinesServiceClient
{
    Task<List<CineViewModel>> ListarAsync();
    Task<CineViewModel?> ObtenerAsync(int id);
    Task<List<SalaViewModel>> ListarSalasPorCineAsync(int idCine);
}
