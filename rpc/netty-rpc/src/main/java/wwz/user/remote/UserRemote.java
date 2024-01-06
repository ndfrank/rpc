package wwz.user.remote;

import wwz.netty.util.Response;
import wwz.user.bean.User;

import java.util.List;

public interface UserRemote {
    public Response saveUser(User user);
    public Response saveUsers(List<User> users);
}
