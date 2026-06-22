using CineFlow.Web.ViewModels.User;
using System.Net.Http;

namespace CineFlow.Web.Servicios.User;

public class UserServiceRestClient : IUserServiceClient
{
    private readonly HttpClient _httpClient;
    private readonly UserState _userState;

    public UserServiceRestClient(HttpClient httpClient, UserState userState)
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
        public string Telefono { get; set; } = string.Empty;
        public DateTime? FechaNacimiento { get; set; }
    }

    public async Task<UserProfileViewModel> GetProfileAsync()
    {
        if (!_userState.IsLoggedIn || _userState.UserId == null)
        {
            return new UserProfileViewModel();
        }

        try
        {
            var user = await _httpClient.GetFromJsonAsync<UsuarioResponse>($"v1/usuarios/{_userState.UserId}");
            if (user != null)
            {
                return new UserProfileViewModel
                {
                    Id = user.IdUsuario.ToString(),
                    Nombre = user.Nombre,
                    Apellido = user.Apellidos,
                    Email = user.Email,
                    Telefono = user.Telefono,
                    FechaNacimiento = user.FechaNacimiento,
                    FechaRegistro = DateTime.Now // Si la API no retorna fecha, usamos la actual de momento
                };
            }
        }
        catch
        {
            // Fallback si hay error de red
        }

        return new UserProfileViewModel();
    }

    public async Task<bool> UpdateProfileAsync(UserProfileViewModel profile)
    {
        if (!_userState.IsLoggedIn || _userState.UserId == null) return false;

        var payload = new
        {
            nombre = profile.Nombre,
            apellidos = profile.Apellido,
            telefono = profile.Telefono,
            fechaNacimiento = profile.FechaNacimiento?.ToString("yyyy-MM-dd")
        };

        try
        {
            var response = await _httpClient.PutAsJsonAsync($"v1/usuarios/{_userState.UserId}/perfil", payload);
            if (response.IsSuccessStatusCode)
            {
                // Actualizar estado local
                _userState.Login(_userState.UserId.Value, profile.Nombre, profile.Apellido, profile.Email);
                return true;
            }
        }
        catch { }

        return false;
    }
}
