namespace CineFlow.Web.Servicios.User;

public class UserState
{
    public int? UserId { get; private set; }
    public string Nombre { get; private set; } = string.Empty;
    public string Apellidos { get; private set; } = string.Empty;
    public string Email { get; private set; } = string.Empty;
    
    public bool IsLoggedIn => UserId.HasValue;
    public bool IsAdmin => IsLoggedIn && Email.EndsWith("@cineflow.com", StringComparison.OrdinalIgnoreCase);

    public event Action OnChange;

    public void Login(int userId, string nombre, string apellidos, string email)
    {
        UserId = userId;
        Nombre = nombre;
        Apellidos = apellidos;
        Email = email;
        NotifyStateChanged();
    }

    public void Logout()
    {
        UserId = null;
        Nombre = string.Empty;
        Apellidos = string.Empty;
        Email = string.Empty;
        NotifyStateChanged();
    }

    private void NotifyStateChanged() => OnChange?.Invoke();
}
