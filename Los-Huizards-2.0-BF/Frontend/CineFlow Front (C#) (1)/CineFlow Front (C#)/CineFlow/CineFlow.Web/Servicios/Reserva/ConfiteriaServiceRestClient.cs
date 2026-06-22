using CineFlow.Web.ViewModels.Reserva;
using System.Net.Http.Json;
using System.Text.Json;

namespace CineFlow.Web.Servicios.Reserva;

public class ConfiteriaServiceRestClient : IConfiteriaServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly JsonSerializerOptions _options;
    private readonly JsonSerializerOptions _sendOptions;

    public ConfiteriaServiceRestClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
        _options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
        _sendOptions = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase, PropertyNameCaseInsensitive = true };
    }

    public async Task<List<ConfiteriaViewModel>> ListarAsync()
    {
        try
        {
            var response = await _httpClient.GetFromJsonAsync<List<ConfiteriaViewModel>>("v1/confiteria", _options);
            return response ?? new List<ConfiteriaViewModel>();
        }
        catch
        {
            return new List<ConfiteriaViewModel>();
        }
    }

    public async Task<ConfiteriaViewModel?> CrearAsync(ConfiteriaViewModel item)
    {
        var response = await _httpClient.PostAsJsonAsync("v1/confiteria", item, _sendOptions);
        if (!response.IsSuccessStatusCode)
        {
            var body = await response.Content.ReadAsStringAsync();
            throw new HttpRequestException($"HTTP {(int)response.StatusCode}: {body}");
        }
        return await response.Content.ReadFromJsonAsync<ConfiteriaViewModel>(_options);
    }

    public async Task<bool> ActualizarAsync(int id, ConfiteriaViewModel item)
    {
        var response = await _httpClient.PutAsJsonAsync($"v1/confiteria/{id}", item, _sendOptions);
        return response.IsSuccessStatusCode;
    }

    public async Task<bool> EliminarAsync(int id)
    {
        var response = await _httpClient.DeleteAsync($"v1/confiteria/{id}");
        return response.IsSuccessStatusCode;
    }
}
