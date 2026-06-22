using CineFlow.Web.ViewModels.Auth;

namespace CineFlow.Web.Servicios.Auth;

public interface IAuthServiceClient
{
    Task<bool> LoginAsync(LoginViewModel model);
    Task<bool> RegisterAsync(RegisterViewModel model);
    Task LogoutAsync();
}
