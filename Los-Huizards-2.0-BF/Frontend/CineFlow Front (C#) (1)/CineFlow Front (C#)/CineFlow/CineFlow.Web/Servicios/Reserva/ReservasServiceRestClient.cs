using System.Net.Http.Json;
using System.Text.Json;
using CineFlow.Web.ViewModels.Reserva;

namespace CineFlow.Web.Servicios.Reserva;

public class ProcesarPagoResult
{
    public bool Aprobado { get; set; }
    public string? Estado { get; set; }
    public string? Mensaje { get; set; }
}

public interface IReservasServiceClient
{
    Task<ReservaViewModel?> CrearAsync(ReservaViewModel reserva);
    Task<List<ReservaViewModel>> ListarPorUsuarioAsync(int idUsuario);
    Task<ProcesarPagoResult?> ProcesarPagoAsync(int idPago, string metodoPago = "TARJETA",
        string cardNumber = "", string expiry = "", string cvc = "");
    Task<bool> ActualizarMetodoPagoAsync(int idPago, string metodo, double monto);
    Task<bool> ConfirmarPagoAsync(int idPago);
    Task<bool> CancelarReservaAsync(int idReserva);
    Task<bool> PreReservarAsientosAsync(List<int> idsAsientos);
    Task<bool> LiberarAsientosAsync(List<int> idsAsientos);
}

public class ReservasServiceRestClient : IReservasServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly JsonSerializerOptions _options;

    public ReservasServiceRestClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
        _options = new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
            PropertyNameCaseInsensitive = true
        };
    }

    public async Task<ReservaViewModel?> CrearAsync(ReservaViewModel reserva)
    {
        Console.WriteLine($"[PAYLOAD ENVIADO]: {JsonSerializer.Serialize(reserva, _options)}");
        var response = await _httpClient.PostAsJsonAsync("v1/reservas", reserva, _options);
        if (response.IsSuccessStatusCode)
        {
            return await response.Content.ReadFromJsonAsync<ReservaViewModel>(_options);
        }
        else
        {
            var error = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"[ERROR FROM JAVA]: {error}");
        }
        return null;
    }

    public async Task<List<ReservaViewModel>> ListarPorUsuarioAsync(int idUsuario)
    {
        var response = await _httpClient.GetAsync($"v1/reservas/usuario/{idUsuario}");
        if (response.IsSuccessStatusCode)
        {
            var result = await response.Content.ReadFromJsonAsync<List<ReservaViewModel>>(_options);
            return result ?? new List<ReservaViewModel>();
        }
        return new List<ReservaViewModel>();
    }

    public async Task<ProcesarPagoResult?> ProcesarPagoAsync(int idPago, string metodoPago = "TARJETA",
        string cardNumber = "", string expiry = "", string cvc = "")
    {
        var body = new { metodoPago, cardNumber, expiry, cvc };
        var response = await _httpClient.PostAsJsonAsync($"v1/pagos/{idPago}/procesar", body, _options);
        if (response.IsSuccessStatusCode)
            return await response.Content.ReadFromJsonAsync<ProcesarPagoResult>(_options);
        var err = await response.Content.ReadAsStringAsync();
        Console.WriteLine($"[ERROR procesar pago]: {err}");
        return null;
    }

    public async Task<bool> ActualizarMetodoPagoAsync(int idPago, string metodo, double monto)
    {
        // PUT v1/pagos/{id}: actualiza el metodo elegido (TARJETA / BILLETERA_DIGITAL)
        // antes de procesar. El pago sigue en estado PENDIENTE.
        // 'fecha' es obligatoria en la validacion del backend (no se usa para el UPDATE).
        var body = new
        {
            idPago,
            monto,
            metodo,
            estado = "PENDIENTE",
            fecha = DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss")
        };
        var response = await _httpClient.PutAsJsonAsync($"v1/pagos/{idPago}", body, _options);
        if (!response.IsSuccessStatusCode)
        {
            var err = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"[ERROR actualizar metodo pago]: {err}");
        }
        return response.IsSuccessStatusCode;
    }

    public async Task<bool> ConfirmarPagoAsync(int idPago)
    {
        var response = await _httpClient.PostAsync($"v1/pagos/{idPago}/confirmar", null);
        if (!response.IsSuccessStatusCode)
        {
            var err = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"[ERROR confirmar pago]: {err}");
        }
        return response.IsSuccessStatusCode;
    }

    public async Task<bool> CancelarReservaAsync(int idReserva)
    {
        var response = await _httpClient.PostAsync($"v1/reservas/{idReserva}/cancelar", null);
        if (!response.IsSuccessStatusCode)
        {
            var err = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"[ERROR cancelar reserva]: {err}");
        }
        return response.IsSuccessStatusCode;
    }

    public async Task<bool> PreReservarAsientosAsync(List<int> idsAsientos)
    {
        var response = await _httpClient.PostAsJsonAsync("v1/asientos/pre-reservar", idsAsientos, _options);
        if (!response.IsSuccessStatusCode)
        {
            var err = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"[ERROR pre-reservar asientos]: {err}");
        }
        return response.IsSuccessStatusCode;
    }

    public async Task<bool> LiberarAsientosAsync(List<int> idsAsientos)
    {
        var response = await _httpClient.PostAsJsonAsync("v1/asientos/liberar", idsAsientos, _options);
        if (!response.IsSuccessStatusCode)
        {
            var err = await response.Content.ReadAsStringAsync();
            Console.WriteLine($"[ERROR liberar asientos]: {err}");
        }
        return response.IsSuccessStatusCode;
    }
}
