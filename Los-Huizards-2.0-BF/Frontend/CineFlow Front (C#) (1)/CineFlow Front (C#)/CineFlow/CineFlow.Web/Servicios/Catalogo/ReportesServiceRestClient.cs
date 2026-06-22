using CineFlow.Web.ViewModels.Catalogo;
using System.Net.Http.Json;
using System.Text.Json;

namespace CineFlow.Web.Servicios.Catalogo;

public class ReportesServiceRestClient : IReportesServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly JsonSerializerOptions _options;

    public ReportesServiceRestClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
        _options = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
    }

    public async Task<ReporteVentasPeliculaViewModel?> ObtenerVentasPorPeliculaAsync(int idPelicula)
    {
        try
        {
            return await _httpClient.GetFromJsonAsync<ReporteVentasPeliculaViewModel>($"v1/reportes/ventas/pelicula/{idPelicula}", _options);
        }
        catch
        {
            return null;
        }
    }
}
