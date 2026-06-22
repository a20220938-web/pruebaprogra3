using CineFlow.Web.ViewModels.Catalogo;
using System.Net.Http.Json;
using System.Text.Json;
using Microsoft.AspNetCore.WebUtilities;

namespace CineFlow.Web.Servicios.Catalogo;

public class PeliculasServiceRestClient : IPeliculasServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly JsonSerializerOptions _options;

    public PeliculasServiceRestClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
        _options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
    }

    public async Task<List<PeliculaViewModel>> ListarAsync(string? nombre = null, string? cine = null, string? fecha = null, string? genero = null)
    {
        var url = "v1/peliculas";
        var queryParams = new Dictionary<string, string?>();
        if (!string.IsNullOrEmpty(nombre)) queryParams.Add("nombre", nombre);
        if (!string.IsNullOrEmpty(cine)) queryParams.Add("cine", cine);
        if (!string.IsNullOrEmpty(fecha)) queryParams.Add("fecha", fecha);
        if (!string.IsNullOrEmpty(genero)) queryParams.Add("genero", genero);

        var requestUrl = QueryHelpers.AddQueryString(url, queryParams);
        try
        {
            var response = await _httpClient.GetFromJsonAsync<List<PeliculaViewModel>>(requestUrl, _options);
            return response ?? new List<PeliculaViewModel>();
        }
        catch (HttpRequestException ex)
        {
            Console.WriteLine($"[Error de Conexión] No se pudo conectar al Backend Java. ¿Está encendido el servidor? Detalle: {ex.Message}");
            return new List<PeliculaViewModel>();
        }
    }

    public async Task<PeliculaViewModel?> ObtenerAsync(int id)
    {
        try
        {
            try
            {
                var response = await _httpClient.GetFromJsonAsync<PeliculaViewModel>($"v1/peliculas/{id}", _options);
                return response;
            }
            catch (HttpRequestException ex)
            {
                Console.WriteLine($"[Error de Conexión] No se pudo conectar al Backend Java. Detalle: {ex.Message}");
                return null;
            }
        }
        catch (HttpRequestException ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            return null;
        }
    }

    public async Task<List<FuncionViewModel>> ListarFuncionesAsync(int id)
    {
        var funciones = await _httpClient.GetFromJsonAsync<List<FuncionViewModel>>($"v1/peliculas/{id}/funciones", _options);
        return funciones ?? new List<FuncionViewModel>();
    }

    public async Task<PeliculaViewModel?> CrearAsync(PeliculaViewModel pelicula)
    {
        var sendOptions = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase, PropertyNameCaseInsensitive = true };
        var response = await _httpClient.PostAsJsonAsync("v1/peliculas", pelicula, sendOptions);
        if (!response.IsSuccessStatusCode) return null;
        return await response.Content.ReadFromJsonAsync<PeliculaViewModel>(_options);
    }

    public async Task<bool> ActualizarAsync(int id, PeliculaViewModel pelicula)
    {
        var sendOptions = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase, PropertyNameCaseInsensitive = true };
        var response = await _httpClient.PutAsJsonAsync($"v1/peliculas/{id}", pelicula, sendOptions);
        return response.IsSuccessStatusCode;
    }

    public async Task<bool> EliminarAsync(int id)
    {
        var response = await _httpClient.DeleteAsync($"v1/peliculas/{id}");
        return response.IsSuccessStatusCode;
    }
}
