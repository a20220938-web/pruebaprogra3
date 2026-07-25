using System.Net.Http.Json;
using System.Text.Json;
using CineFlow.Web.ViewModels.Reserva;

namespace CineFlow.Web.Servicios.Reserva;

public class InventarioServiceRestClient : IInventarioServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly JsonSerializerOptions _options;
    private readonly JsonSerializerOptions _sendOptions;

    public InventarioServiceRestClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
        _options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
        _sendOptions = new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
            PropertyNameCaseInsensitive = true
        };
    }

    public async Task<List<InventarioViewModel>> ListarAsync()
    {
        try
        {
            var r = await _httpClient.GetFromJsonAsync<List<InventarioViewModel>>("v1/inventario", _options);
            return r ?? new();
        }
        catch { return new(); }
    }

    public async Task<bool> ActualizarAsync(int id, InventarioViewModel inventario)
    {
        var resp = await _httpClient.PutAsJsonAsync($"v1/inventario/{id}", inventario, _sendOptions);
        return resp.IsSuccessStatusCode;
    }
}
