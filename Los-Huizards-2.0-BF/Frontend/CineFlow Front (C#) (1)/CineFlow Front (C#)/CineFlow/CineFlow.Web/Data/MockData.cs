using System;
using System.Collections.Generic;
using System.Linq;

namespace CineFlow.Web.Data
{
    public class MockCine
    {
        public string IdCine { get; set; }
        public string Nombre { get; set; }
        public string Ubicacion { get; set; }
    }

    public class MockPelicula
    {
        public string IdPelicula { get; set; }
        public string Titulo { get; set; }
        public string Sinopsis { get; set; }
        public int Duracion { get; set; }
        public string Genero { get; set; }
        public string EdadRestriccion { get; set; }
        public string PosterUrl { get; set; }
    }

    public class MockSala
    {
        public string IdSala { get; set; }
        public int Numero { get; set; }
        public int Filas { get; set; }
        public int ColumnasPerFila { get; set; }
    }

    public class MockAsiento
    {
        public string IdAsiento { get; set; }
        public string Fila { get; set; }
        public int Numero { get; set; }
        public string Estado { get; set; } // DISPONIBLE, OCUPADO, SELECCIONADO
    }

    public class MockFuncion
    {
        public string IdFuncion { get; set; }
        public DateTime FechaHora { get; set; }
        public decimal PrecioBase { get; set; }
        public MockSala Sala { get; set; }
        public MockPelicula Pelicula { get; set; }
        public string Formato { get; set; }
        public List<MockAsiento> Asientos { get; set; }
        public MockCine Cine { get; set; }
    }

    public class MockArticuloIndividual
    {
        public string IdItem { get; set; }
        public string Nombre { get; set; }
        public string Descripcion { get; set; }
        public string Categoria { get; set; }
        public decimal PrecioUnitario { get; set; }
        public int Stock { get; set; }
    }

    public class MockUsuario
    {
        public string Nombres { get; set; } = "Juan";
        public string Apellidos { get; set; } = "Pérez García";
        public string Email { get; set; } = "usuario@example.com";
        public string Password { get; set; } = "123456";
        public string Telefono { get; set; } = "987654321";
        public DateTime FechaNacimiento { get; set; } = new DateTime(1995, 3, 15);
        public string Role { get; set; } = "User"; // "User" o "Admin"
    }

    public class MockReserva
    {
        public string ReservaId { get; set; }
        public DateTime FechaCompra { get; set; }
        public MockFuncion Funcion { get; set; }
        public List<MockAsiento> Asientos { get; set; }
        public Dictionary<string, string> TiposEntrada { get; set; }
        public Dictionary<MockArticuloIndividual, int> Snacks { get; set; }
        public decimal TotalPagado { get; set; }
    }

    public static class MockDataStore
    {
        private static readonly string profileFilePath = "profile.json";

        // MOCK SHOPPING CART (Para mantener estado entre pantallas de compra)
        public static string CurrentFuncionId { get; set; }
        public static List<MockAsiento> CurrentSeats { get; set; } = new List<MockAsiento>();
        public static Dictionary<string, string> CurrentTicketTypes { get; set; } = new Dictionary<string, string>();
        public static Dictionary<MockArticuloIndividual, int> CurrentSnacks { get; set; } = new Dictionary<MockArticuloIndividual, int>();

        // RESERVAS COMPLETADAS
        public static List<MockReserva> Reservas { get; set; } = new List<MockReserva>();

        // USUARIOS DEL SISTEMA
        public static List<MockUsuario> Usuarios { get; set; } = new List<MockUsuario>
        {
            new MockUsuario { Email = "admin@cineflow.com", Password = "admin", Role = "Admin", Nombres = "Administrador" },
            new MockUsuario { Email = "usuario@example.com", Password = "123", Role = "User", Nombres = "Juan" }
        };

        public static MockUsuario CurrentUser { get; set; }

        static MockDataStore()
        {
            // Crear 2 reservas de prueba ("las dos que ya están")
            var f1 = Funciones[0];
            var f2 = Funciones[1];

            Reservas.Add(new MockReserva
            {
                ReservaId = "RES-001A",
                FechaCompra = DateTime.Now.AddDays(-2),
                Funcion = f1,
                Asientos = new List<MockAsiento> { f1.Asientos[5], f1.Asientos[6] },
                TiposEntrada = new Dictionary<string, string> { { f1.Asientos[5].IdAsiento, "REGULAR" }, { f1.Asientos[6].IdAsiento, "NIÑO" } },
                Snacks = new Dictionary<MockArticuloIndividual, int> { { Snacks[0], 1 }, { Snacks[1], 2 } },
                TotalPagado = (f1.PrecioBase) + (f1.PrecioBase * 0.7m) + (Snacks[0].PrecioUnitario * 1) + (Snacks[1].PrecioUnitario * 2)
            });

            Reservas.Add(new MockReserva
            {
                ReservaId = "RES-002B",
                FechaCompra = DateTime.Now.AddDays(-1),
                Funcion = f2,
                Asientos = new List<MockAsiento> { f2.Asientos[12] },
                TiposEntrada = new Dictionary<string, string> { { f2.Asientos[12].IdAsiento, "REGULAR" } },
                Snacks = new Dictionary<MockArticuloIndividual, int> { { Snacks[2], 1 } },
                TotalPagado = f2.PrecioBase + (Snacks[2].PrecioUnitario * 1)
            });
        }

        public static MockUsuario GetUsuario()
        {
            try
            {
                if (System.IO.File.Exists(profileFilePath))
                {
                    var json = System.IO.File.ReadAllText(profileFilePath);
                    return System.Text.Json.JsonSerializer.Deserialize<MockUsuario>(json) ?? new MockUsuario();
                }
            }
            catch { }
            return new MockUsuario();
        }

        public static void SaveUsuario(MockUsuario usuario)
        {
            try
            {
                var options = new System.Text.Json.JsonSerializerOptions { WriteIndented = true };
                var json = System.Text.Json.JsonSerializer.Serialize(usuario, options);
                System.IO.File.WriteAllText(profileFilePath, json);
            }
            catch { }
        }

        public static List<MockCine> Cines = new List<MockCine>
        {
            new MockCine { IdCine = "1", Nombre = "CineFlow Centro", Ubicacion = "Av. Principal 123, Lima" },
            new MockCine { IdCine = "2", Nombre = "CineFlow Norte", Ubicacion = "Av. Los Olivos 456, Lima" }
        };

        public static List<MockPelicula> Peliculas = new List<MockPelicula>
        {
            new MockPelicula { IdPelicula = "1", Titulo = "El Último Refugio", Sinopsis = "En un futuro post-apocalíptico...", Duracion = 142, Genero = "Ciencia Ficción", EdadRestriccion = "+13", PosterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=400" },
            new MockPelicula { IdPelicula = "2", Titulo = "Risas en el Parque", Sinopsis = "Una comedia sobre un grupo de amigos...", Duracion = 98, Genero = "Comedia", EdadRestriccion = "ATP", PosterUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=400" },
            new MockPelicula { IdPelicula = "3", Titulo = "La Casa del Terror", Sinopsis = "Una familia se muda a una casa antigua...", Duracion = 105, Genero = "Terror", EdadRestriccion = "+16", PosterUrl = "https://images.unsplash.com/photo-1518676590629-3dcbd9c5a5c9?w=400" },
            new MockPelicula { IdPelicula = "4", Titulo = "Aventuras en el Bosque", Sinopsis = "Dos niños descubren un mundo mágico...", Duracion = 92, Genero = "Animación", EdadRestriccion = "ATP", PosterUrl = "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=400" },
            new MockPelicula { IdPelicula = "5", Titulo = "Operación Rescate", Sinopsis = "Un equipo de élite debe rescatar a un grupo...", Duracion = 128, Genero = "Acción", EdadRestriccion = "+16", PosterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=400" },
            new MockPelicula { IdPelicula = "6", Titulo = "El Violinista", Sinopsis = "La historia de un músico...", Duracion = 116, Genero = "Drama", EdadRestriccion = "+13", PosterUrl = "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=400" },
            new MockPelicula { IdPelicula = "7", Titulo = "EN BUSCA DE SER BAJO", Sinopsis = "“EN BUSCA DE SER BAJO” es la inspiradora historia de un alumno universitario llamado “Chino”, un joven con un sueño poco común: convertirse en el alumno más bajo de su universidad. En un entorno donde todos compiten por destacar de manera tradicional, Chino desafía las expectativas y enfrenta burlas, dudas y sus propias inseguridades mientras lucha por demostrar que la grandeza no se mide en estatura, sino en determinación y valentía; en su camino descubre que aceptar quién es y perseguir sus metas, por más inusuales que parezcan, es lo que verdaderamente lo hace sobresalir.", Duracion = 110, Genero = "Drama", EdadRestriccion = "ATP", PosterUrl = "poster-ser-bajo.jpeg" },
            new MockPelicula { IdPelicula = "8", Titulo = "Mulan: El Holguin", Sinopsis = "Año 2026. Rodrigo Holguín está a punto de aceptar la solicitud de conexión en LinkedIn que catapultará su carrera profesional, cuando un inesperado bug cuántico, mientras compila un complejo código en C++ en su terminal de Linux, genera un vórtice espacio-temporal. En un parpadeo, es succionado por el monitor y escupido en la antigua China, aterrizando de cara justo en medio del reclutamiento para el Ejército Imperial. Sin Wi-Fi y armado únicamente con una sartén antiadherente, Rodrigo deberá enfrentarse al temible Shan Yu usando metodologías Agile y optimizando las raciones de arroz con algoritmos de bajo nivel.", Duracion = 125, Genero = "Comedia", EdadRestriccion = "ATP", PosterUrl = "poster-mulan-holguin.png" }
        };

        public static List<MockSala> Salas = new List<MockSala>
        {
            new MockSala { IdSala = "1", Numero = 1, Filas = 8, ColumnasPerFila = 12 },
            new MockSala { IdSala = "2", Numero = 2, Filas = 10, ColumnasPerFila = 14 },
            new MockSala { IdSala = "3", Numero = 3, Filas = 6, ColumnasPerFila = 10 }
        };

        private static List<MockAsiento> CreateAsientos(int filas, int cols)
        {
            var asientos = new List<MockAsiento>();
            string letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            var rand = new Random();
            for (int i = 0; i < filas; i++)
            {
                for (int j = 1; j <= cols; j++)
                {
                    asientos.Add(new MockAsiento
                    {
                        IdAsiento = $"{letras[i]}{j}",
                        Fila = letras[i].ToString(),
                        Numero = j,
                        Estado = rand.NextDouble() > 0.3 ? "DISPONIBLE" : "OCUPADO"
                    });
                }
            }
            return asientos;
        }

        public static List<MockFuncion> Funciones = new List<MockFuncion>
        {
            new MockFuncion { IdFuncion = "1", FechaHora = DateTime.Now.AddHours(2), PrecioBase = 25, Sala = Salas[0], Pelicula = Peliculas[0], Formato = "2D", Asientos = CreateAsientos(8, 12), Cine = Cines[0] },
            new MockFuncion { IdFuncion = "2", FechaHora = DateTime.Now.AddHours(5), PrecioBase = 30, Sala = Salas[1], Pelicula = Peliculas[0], Formato = "3D", Asientos = CreateAsientos(10, 14), Cine = Cines[1] },
            new MockFuncion { IdFuncion = "3", FechaHora = DateTime.Now.AddHours(3), PrecioBase = 25, Sala = Salas[0], Pelicula = Peliculas[1], Formato = "2D", Asientos = CreateAsientos(8, 12), Cine = Cines[0] },
            new MockFuncion { IdFuncion = "4", FechaHora = DateTime.Now.AddHours(4), PrecioBase = 25, Sala = Salas[2], Pelicula = Peliculas[6], Formato = "2D", Asientos = CreateAsientos(6, 10), Cine = Cines[1] },
            new MockFuncion { IdFuncion = "5", FechaHora = DateTime.Now.AddHours(6), PrecioBase = 28, Sala = Salas[1], Pelicula = Peliculas[7], Formato = "2D", Asientos = CreateAsientos(10, 14), Cine = Cines[0] }
        };

        public static List<MockArticuloIndividual> Snacks = new List<MockArticuloIndividual>
        {
            new MockArticuloIndividual { IdItem = "1", Nombre = "Popcorn Grande", Descripcion = "Palomitas de maíz tamaño grande", Categoria = "Snack", PrecioUnitario = 12, Stock = 50 },
            new MockArticuloIndividual { IdItem = "2", Nombre = "Gaseosa 500ml", Descripcion = "Bebida gaseosa de 500ml", Categoria = "Bebida", PrecioUnitario = 8, Stock = 80 },
            new MockArticuloIndividual { IdItem = "3", Nombre = "Nachos con Queso", Descripcion = "Nachos con salsa de queso cheddar", Categoria = "Snack", PrecioUnitario = 15, Stock = 25 }
        };
    }
}
