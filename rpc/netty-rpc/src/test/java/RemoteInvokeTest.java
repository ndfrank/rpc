import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wwz.netty.annotation.RemoteInvoke;
import wwz.user.bean.User;
import wwz.user.remote.UserRemote;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokeTest.class)
@ComponentScan("wwz")
public class RemoteInvokeTest {
    @RemoteInvoke
    private UserRemote userRemote;

    @Test
    public void testSaveUser() {
        User u = new User();
        u.setId(1);
        u.setName("张三");
        userRemote.saveUser(u);
    }

    @Test
    public void testSaveUsers() {
        List<User> users = new ArrayList<>();
        User u = new User();
        u.setId(1);
        u.setName("张三");
        users.add(u);
        userRemote.saveUsers(users);
    }
}
