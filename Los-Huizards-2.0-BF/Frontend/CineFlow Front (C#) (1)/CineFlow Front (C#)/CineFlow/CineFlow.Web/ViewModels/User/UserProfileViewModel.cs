namespace CineFlow.Web.ViewModels.User;

public class UserProfileViewModel
{
    public string Id { get; set; } = string.Empty;
    public string Nombre { get; set; } = string.Empty;
    public string Apellido { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Telefono { get; set; } = string.Empty;
    public DateTime? FechaNacimiento { get; set; }
    public DateTime FechaRegistro { get; set; }
}
