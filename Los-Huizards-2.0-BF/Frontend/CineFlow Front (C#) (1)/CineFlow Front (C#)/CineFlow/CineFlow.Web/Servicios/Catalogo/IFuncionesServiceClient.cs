using CineFlow.Web.ViewModels.Catalogo;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CineFlow.Web.Servicios.Catalogo;

public interface IFuncionesServiceClient
{
    Task<List<FuncionViewModel>> ListarAsync();
    Task<FuncionViewModel?> ObtenerAsync(int id);
    Task<List<FuncionViewModel>> ListarPorPeliculaAsync(int idPelicula);
    Task<List<AsientoViewModel>> ListarAsientosAsync(int idFuncion);
}
