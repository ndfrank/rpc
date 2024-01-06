package wwz.user.service;

import org.springframework.stereotype.Service;
import wwz.user.bean.User;

import java.util.List;
@Service
public class UserService {
    public void save(User user) {
        System.out.println("调用了UserService.save");
    }
    public void saveList(List<User> users) {
        System.out.println("调用了UserService.saveList");
    }
}
