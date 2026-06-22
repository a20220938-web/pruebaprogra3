using CineFlow.Web.ViewModels.Auth;
using System.Net.Http;
using System.Net.Http.Json;

namespace CineFlow.Web.Servicios.Auth;

public class AuthServiceRestClient : IAuthServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly CineFlow.Web.Servicios.User.UserState _userState;

    public AuthServiceRestClient(HttpClient httpClient, CineFlow.Web.Servicios.User.UserState userState)
    {
        _httpClient = httpClient;
        _userState = userState;
    }

    private class UsuarioResponse
    {
        public int IdUsuario { get; set; }
        public string Nombre { get; set; } = string.Empty;
        public string Apellidos { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
    }

    public async Task<bool> LoginAsync(LoginViewModel model)
    {
        var credenciales = new { email = model.Email, contrasenia = model.Password };
        try
        {
            var response = await _httpClient.PostAsJsonAsync("v1/auth/login", credenciales);
            if (response.IsSuccessStatusCode)
            {
                var user = await response.Content.ReadFromJsonAsync<UsuarioResponse>();
                if (user != null)
                {
                    var userEmail = string.IsNullOrWhiteSpace(user.Email) ? model.Email : user.Email;
                    _userState.Login(user.IdUsuario, user.Nombre, user.Apellidos, userEmail);
                    return true;
                }
            }
            return false;
        }
        catch
        {
            return false;
        }
    }

    public async Task<bool> RegisterAsync(RegisterViewModel model)
    {
        var usuario = new 
        { 
            nombre = model.Nombre, 
            apellidos = model.Apellido, 
            email = model.Email, 
            contrasenia = model.Password,
            telefono = "" // El backend lanza error si esto es nulo al hacer login
        };
        try
        {
            var response = await _httpClient.PostAsJsonAsync("v1/usuarios", usuario);
            if (response.IsSuccessStatusCode)
            {
                return true;
            }
            else
            {
                var errorMsg = await response.Content.ReadAsStringAsync();
                throw new Exception($"Error de API: {response.StatusCode} - {errorMsg}");
            }
        }
        catch (Exception ex)
        {
            throw new Exception("Error al registrar: " + ex.Message);
        }
    }

    public Task LogoutAsync()
    {
        _userState.Logout();
        return Task.CompletedTask;
    }
}
