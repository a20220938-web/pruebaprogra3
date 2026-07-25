using CineFlow.Web.ViewModels.Catalogo;
using System.Net.Http.Json;
using System.Text.Json;

namespace CineFlow.Web.Servicios.Catalogo;

public class CinesServiceRestClient : ICinesServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly JsonSerializerOptions _options;

    public CinesServiceRestClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
        _options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
    }

    public async Task<List<CineViewModel>> ListarAsync()
    {
        try
        {
            var response = await _httpClient.GetFromJsonAsync<List<CineViewModel>>("v1/cines", _options);
            return response ?? new List<CineViewModel>();
        }
        catch (HttpRequestException ex)
        {
            Console.WriteLine($"[Error de Conexión] No se pudo conectar al Backend Java. Detalle: {ex.Message}");
            return new List<CineViewModel>();
        }
    }

    public async Task<CineViewModel?> ObtenerAsync(int id)
    {
        try
        {
            return await _httpClient.GetFromJsonAsync<CineViewModel>($"v1/cines/{id}", _options);
        }
        catch (HttpRequestException ex)
        {
            Console.WriteLine($"[Error de Conexión] No se pudo conectar al Backend Java. Detalle: {ex.Message}");
            return null;
        }
    }

    public async Task<List<SalaViewModel>> ListarSalasPorCineAsync(int idCine)
    {
        try
        {
            var response = await _httpClient.GetFromJsonAsync<List<SalaViewModel>>($"v1/cines/{idCine}/salas", _options);
            return response ?? new List<SalaViewModel>();
        }
        catch (HttpRequestException ex)
        {
            Console.WriteLine($"[Error de Conexión] No se pudo conectar al Backend Java. Detalle: {ex.Message}");
            return new List<SalaViewModel>();
        }
    }
}
