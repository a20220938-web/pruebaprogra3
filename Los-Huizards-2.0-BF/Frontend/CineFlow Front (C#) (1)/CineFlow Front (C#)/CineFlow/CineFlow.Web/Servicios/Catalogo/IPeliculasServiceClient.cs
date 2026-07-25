using CineFlow.Web.ViewModels.Catalogo;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CineFlow.Web.Servicios.Catalogo;

public interface IPeliculasServiceClient
{
    Task<List<PeliculaViewModel>> ListarAsync(string? nombre = null, string? cine = null, string? fecha = null, string? genero = null);
    Task<PeliculaViewModel?> ObtenerAsync(int id);
    Task<List<FuncionViewModel>> ListarFuncionesAsync(int id);
    Task<PeliculaViewModel?> CrearAsync(PeliculaViewModel pelicula);
    Task<bool> ActualizarAsync(int id, PeliculaViewModel pelicula);
    Task<bool> EliminarAsync(int id);

}
