using System;

namespace CineFlow.Web.ViewModels.Catalogo;

public class FuncionViewModel
{
    public int Id { get; set; }
    public DateTime FechaHora { get; set; }
    public double PrecioBase { get; set; }
    public PeliculaViewModel? Pelicula { get; set; }
    public SalaViewModel? Sala { get; set; }
    public string? Formato { get; set; } // Map as string from JSON
    public List<AsientoViewModel>? MapaAsientos { get; set; }
}
