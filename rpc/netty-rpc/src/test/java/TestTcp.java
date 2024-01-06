import wwz.netty.client.ClientRequest;
import wwz.netty.util.Response;
import wwz.netty.client.TcpClient;
import org.junit.Test;
import wwz.user.bean.User;

import java.util.ArrayList;
import java.util.List;

public class TestTcp {
    @Test
    public void testGetResponse() {
        ClientRequest request = new ClientRequest();
        request.setContent("测试tcp长连接");
        Response rep = TcpClient.send(request);
        System.out.println(rep.getResult());
    }

    @Test
    public void testSaveUser() {
        ClientRequest request = new ClientRequest();
        User u = new User();
        u.setId(1);
        u.setName("张三");
        request.setCommand("wwz.user.controller.UserController.saveUser");
        request.setContent(u);
        Response rep = TcpClient.send(request);
        System.out.println(rep.getResult());
    }

    @Test
    public void testSaveUsers() {
        ClientRequest request = new ClientRequest();
        List<User> users = new ArrayList<>();
        User u = new User();
        u.setId(1);
        u.setName("张三");
        users.add(u);
        request.setCommand("wwz.user.controller.UserController.saveUsers");
        request.setContent(users);
        Response rep = TcpClient.send(request);
        System.out.println(rep.getResult());
    }
}
