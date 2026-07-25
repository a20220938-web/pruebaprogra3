using CineFlow.Web.ViewModels.User;

namespace CineFlow.Web.Servicios.User;

public interface IUserServiceClient
{
    Task<UserProfileViewModel> GetProfileAsync();
    Task<bool> UpdateProfileAsync(UserProfileViewModel profile);
}
