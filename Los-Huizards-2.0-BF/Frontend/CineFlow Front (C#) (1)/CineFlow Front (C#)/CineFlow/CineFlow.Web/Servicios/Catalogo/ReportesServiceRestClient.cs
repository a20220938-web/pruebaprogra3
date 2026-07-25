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

    public async Task<ReporteVentasPeliculaViewModel?> ObtenerVentasPorPeliculaAsync(int idPelicula, DateTime? fechaInicio = null, DateTime? fechaFin = null)
    {
        try
        {
            var url = $"v1/reportes/ventas/pelicula/{idPelicula}";
            var queryParams = new List<string>();

            if (fechaInicio.HasValue)
                queryParams.Add($"fechaInicio={fechaInicio.Value:yyyy-MM-dd}");
            if (fechaFin.HasValue)
                queryParams.Add($"fechaFin={fechaFin.Value:yyyy-MM-dd}");

            if (queryParams.Count > 0)
                url += "?" + string.Join("&", queryParams);

            return await _httpClient.GetFromJsonAsync<ReporteVentasPeliculaViewModel>(url, _options);
        }
        catch
        {
            return null;
        }
    }
}
