package wwz.user.controller;

import wwz.netty.util.Response;
import wwz.netty.util.ResponseUtil;
import wwz.user.bean.User;
import org.springframework.stereotype.Controller;
import wwz.user.service.UserService;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class UserController {
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
