using CineFlow.Web.ViewModels.Auth;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text.Json;

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
            telefono = model.Telefono,
            fechaNacimiento = model.FechaNacimiento?.ToString("yyyy-MM-dd")
        };
        try
        {
            var response = await _httpClient.PostAsJsonAsync("v1/usuarios", usuario);
            if (response.IsSuccessStatusCode)
            {
                return true;
            }
            else if (response.StatusCode == System.Net.HttpStatusCode.Conflict)
            {
                throw new Exception("El correo ingresado ya está registrado.");
            }
            else
            {
                throw new Exception("No se pudo registrar la cuenta. Intente nuevamente.");
            }
        }
        catch (Exception) when (true)
        {
            throw;
        }
    }

    public Task LogoutAsync()
    {
        _userState.Logout();
        return Task.CompletedTask;
    }
}
