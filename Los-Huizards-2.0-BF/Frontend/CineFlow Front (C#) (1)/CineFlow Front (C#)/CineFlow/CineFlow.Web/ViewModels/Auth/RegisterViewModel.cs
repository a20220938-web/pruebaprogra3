namespace CineFlow.Web.ViewModels.Auth;

using System.ComponentModel.DataAnnotations;

public class RegisterViewModel
{
    [Required(ErrorMessage = "El nombre es requerido")]
    public string Nombre { get; set; } = string.Empty;

    [Required(ErrorMessage = "El apellido es requerido")]
    public string Apellido { get; set; } = string.Empty;

    [Required(ErrorMessage = "El correo electrónico es requerido")]
    [RegularExpression(
        @"^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.com$",
        ErrorMessage = "Ingresa un correo válido con dominio .com (ej: nombre@ejemplo.com)")]
    public string Email { get; set; } = string.Empty;

    [Required(ErrorMessage = "La contraseña es requerida")]
    [RegularExpression(
        @"^(?=.*[!@#$%^&*()\-_=+\[\]{};':""\\|,.<>\/?]).{6,}$",
        ErrorMessage = "Mínimo 6 caracteres y al menos 1 carácter especial (!@#$%^&*...)")]
    public string Password { get; set; } = string.Empty;

    [Compare(nameof(Password), ErrorMessage = "Las contraseñas no coinciden")]
    public string ConfirmPassword { get; set; } = string.Empty;
}
