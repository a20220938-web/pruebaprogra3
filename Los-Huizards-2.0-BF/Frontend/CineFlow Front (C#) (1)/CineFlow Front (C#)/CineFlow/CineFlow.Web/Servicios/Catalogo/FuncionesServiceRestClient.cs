using CineFlow.Web.ViewModels.Catalogo;
using System.Net.Http.Json;
using System.Text.Json;
using System.Collections.Generic;

namespace CineFlow.Web.Servicios.Catalogo;

public class FuncionesServiceRestClient : IFuncionesServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly JsonSerializerOptions _options;

    public FuncionesServiceRestClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
        _options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
    }

    public async Task<List<FuncionViewModel>> ListarAsync()
    {
        var funciones = await _httpClient.GetFromJsonAsync<List<FuncionViewModel>>("v1/funciones", _options);
        return funciones ?? new List<FuncionViewModel>();
    }

    public async Task<FuncionViewModel?> ObtenerAsync(int id)
    {
        try
        {
            return await _httpClient.GetFromJsonAsync<FuncionViewModel>($"v1/funciones/{id}", _options);
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return null;
        }
    }

    public async Task<List<FuncionViewModel>> ListarPorPeliculaAsync(int idPelicula)
    {
        try
        {
            var funciones = await _httpClient.GetFromJsonAsync<List<FuncionViewModel>>($"v1/funciones/pelicula/{idPelicula}", _options);
            return funciones ?? new List<FuncionViewModel>();
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return new List<FuncionViewModel>();
        }
    }

    public async Task<List<AsientoViewModel>> ListarAsientosAsync(int idFuncion)
    {
        try
        {
            var asientos = await _httpClient.GetFromJsonAsync<List<AsientoViewModel>>($"v1/funciones/{idFuncion}/asientos", _options);
            return asientos ?? new List<AsientoViewModel>();
        }
        catch
        {
            return new List<AsientoViewModel>();
        }
    }
}
