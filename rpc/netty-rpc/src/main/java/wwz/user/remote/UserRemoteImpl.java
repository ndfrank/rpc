package wwz.user.remote;

import wwz.netty.annotation.Remote;
import wwz.netty.util.Response;
import wwz.netty.util.ResponseUtil;
import wwz.user.bean.User;
import wwz.user.service.UserService;

import javax.annotation.Resource;
import java.util.List;

@Remote
public class UserRemoteImpl implements UserRemote{
    @Resource
    private UserService userService;

    public Response saveUser(User user) {
        userService.save(user);
        return ResponseUtil.createSuccesssResult(user);
    }

    public Response saveUsers(List<User> users) {
        userService.saveList(users);
        return ResponseUtil.createSuccesssResult(users);
    }
}
