using System.Security.Claims;
using System.Text.Json;
using CineFlow.Web.Components;
using CineFlow.Web.Extensiones;
using CineFlow.Web.Servicios.Auth;
using CineFlow.Web.Servicios.User;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.DataProtection;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Environment.EnvironmentName = "Development"; // Forzar Development temporalmente para que cargue los assets

var apiUrl = builder.Configuration.GetValue<string>("ApiUrl") ?? "http://localhost:8080/CineFlowRS-1.0-SNAPSHOT/api/";

builder.Services.AddHttpClient();

// Autenticación de CineFlow
builder.Services.AddCineFlowAuthentication();

// Llaves de Data Protection efímeras (solo en memoria): al reiniciar la app,
// las cookies de sesión emitidas antes dejan de ser válidas y se cierra la sesión.
builder.Services.AddDataProtection().UseEphemeralDataProtectionProvider();

// Inyección de dependencias de Negocio y Servicios API REST
builder.Services.AddHttpClient<IAuthServiceClient, AuthServiceRestClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl);
});
builder.Services.AddHttpClient<IUserServiceClient, UserServiceRestClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl);
});

// Estado de sesión del usuario
builder.Services.AddScoped<CineFlow.Web.Servicios.User.UserState>();

// Configuración de REST Clients para el backend Java
builder.Services.AddHttpClient<CineFlow.Web.Servicios.Catalogo.IPeliculasServiceClient, CineFlow.Web.Servicios.Catalogo.PeliculasServiceRestClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl);
});
builder.Services.AddHttpClient<CineFlow.Web.Servicios.Catalogo.ICinesServiceClient, CineFlow.Web.Servicios.Catalogo.CinesServiceRestClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl);
});
builder.Services.AddHttpClient<CineFlow.Web.Servicios.Catalogo.IFuncionesServiceClient, CineFlow.Web.Servicios.Catalogo.FuncionesServiceRestClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl);
});
builder.Services.AddHttpClient<CineFlow.Web.Servicios.Reserva.IConfiteriaServiceClient, CineFlow.Web.Servicios.Reserva.ConfiteriaServiceRestClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl);
});
builder.Services.AddHttpClient<CineFlow.Web.Servicios.Reserva.IReservasServiceClient, CineFlow.Web.Servicios.Reserva.ReservasServiceRestClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl);
    client.Timeout = TimeSpan.FromMinutes(5);
});
builder.Services.AddHttpClient<CineFlow.Web.Servicios.Catalogo.IReportesServiceClient, CineFlow.Web.Servicios.Catalogo.ReportesServiceRestClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl);
});
builder.Services.AddHttpClient<CineFlow.Web.Servicios.Reserva.IInventarioServiceClient, CineFlow.Web.Servicios.Reserva.InventarioServiceRestClient>(client =>
{
    client.BaseAddress = new Uri(apiUrl);
});

// Estado para el flujo de reserva
builder.Services.AddScoped<CineFlow.Web.Servicios.Reserva.BookingState>();
builder.Services.AddScoped<CineFlow.Web.Servicios.Reserva.ReservasCacheService>();

builder.Services.AddRazorComponents()
    .AddInteractiveServerComponents();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error", createScopeForErrors: true);
    app.UseHsts();
}
app.UseStatusCodePagesWithReExecute("/not-found", createScopeForStatusCodePages: true);
app.UseHttpsRedirection();

// Middlewares de Autenticación/Autorización
app.UseCineFlowAuth();

app.UseAntiforgery();

// Endpoint real de login — escribe la cookie de autenticación
app.MapPost("/account/login", async (HttpContext ctx, IHttpClientFactory factory) =>
{
    var form = await ctx.Request.ReadFormAsync();
    var email = form["email"].ToString();
    var password = form["password"].ToString();
    var returnUrl = form["returnUrl"].ToString();

    using var client = factory.CreateClient();
    client.BaseAddress = new Uri(apiUrl);
    var resp = await client.PostAsJsonAsync("v1/auth/login", new { email, contrasenia = password });
    if (!resp.IsSuccessStatusCode)
        return Results.Redirect("/login?error=1");

    var jsonOpts = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };
    var user = await resp.Content.ReadFromJsonAsync<AccountLoginDto>(jsonOpts);
    if (user == null)
        return Results.Redirect("/login?error=1");

    var userEmail = string.IsNullOrWhiteSpace(user.Email) ? email : user.Email;
    var claims = new List<Claim>
    {
        new("userId",         user.IdUsuario.ToString()),
        new(ClaimTypes.Name,  user.Nombre    ?? ""),
        new("apellidos",      user.Apellidos ?? ""),
        new(ClaimTypes.Email, userEmail),
    };
    var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
    await ctx.SignInAsync(CookieAuthenticationDefaults.AuthenticationScheme, new ClaimsPrincipal(identity));

    var redirect = !string.IsNullOrEmpty(returnUrl) && returnUrl.StartsWith("/") ? returnUrl : "/";
    return Results.Redirect(redirect);
}).DisableAntiforgery();

// Endpoint real de logout — borra la cookie
app.MapPost("/account/logout", async (HttpContext ctx) =>
{
    await ctx.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);
    return Results.Redirect("/login");
}).DisableAntiforgery();

app.UseStaticFiles();
app.MapStaticAssets();
app.MapRazorComponents<App>()
    .AddInteractiveServerRenderMode();

app.Run();

internal sealed class AccountLoginDto
{
    public int    IdUsuario  { get; set; }
    public string? Nombre    { get; set; }
    public string? Apellidos { get; set; }
    public string? Email     { get; set; }
}
