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

    public async Task<FuncionViewModel?> CrearAsync(FuncionViewModel funcion)
    {
        var sendOptions = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase, PropertyNameCaseInsensitive = true };
        sendOptions.Converters.Add(new IsoDateTimeConverter());
        var response = await _httpClient.PostAsJsonAsync("v1/funciones", funcion, sendOptions);
        if (response.IsSuccessStatusCode)
        {
            return await response.Content.ReadFromJsonAsync<FuncionViewModel>(_options);
        }
        else
        {
            var error = await response.Content.ReadAsStringAsync();
            throw new Exception($"[API Error]: {error}");
        }
    }

    public async Task<bool> ActualizarAsync(int id, FuncionViewModel funcion)
    {
        var sendOptions = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase, PropertyNameCaseInsensitive = true };
        sendOptions.Converters.Add(new IsoDateTimeConverter());
        var response = await _httpClient.PutAsJsonAsync($"v1/funciones/{id}", funcion, sendOptions);
        return response.IsSuccessStatusCode;
    }

    private class IsoDateTimeConverter : System.Text.Json.Serialization.JsonConverter<DateTime>
    {
        public override DateTime Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            return DateTime.Parse(reader.GetString()!);
        }

        public override void Write(Utf8JsonWriter writer, DateTime value, JsonSerializerOptions options)
        {
            writer.WriteStringValue(value.ToString("yyyy-MM-ddTHH:mm:ss"));
        }
    }

    public async Task<bool> EliminarAsync(int id)
    {
        var response = await _httpClient.DeleteAsync($"v1/funciones/{id}");
        return response.IsSuccessStatusCode;
    }
}
