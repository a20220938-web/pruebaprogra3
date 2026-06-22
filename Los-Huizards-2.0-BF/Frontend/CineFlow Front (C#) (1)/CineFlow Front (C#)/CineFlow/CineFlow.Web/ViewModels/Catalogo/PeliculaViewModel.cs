namespace CineFlow.Web.ViewModels.Catalogo;

public class PeliculaViewModel
{
    public int Id { get; set; }
    public string? Titulo { get; set; }
    public int Duracion { get; set; }
    public string? Sinopsis { get; set; }
    public string? EdadRestriccion { get; set; }
    public string? Genero { get; set; }
    public string? PosterUrl { get; set; }
}
