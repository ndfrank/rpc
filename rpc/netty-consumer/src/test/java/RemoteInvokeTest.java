
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wwz.annotation.RemoteInvoke;

import wwz.param.Response;
import wwz.user.bean.User;
import wwz.user.remote.UserRemote;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokeTest.class)
@ComponentScan("//")
public class RemoteInvokeTest {
    @RemoteInvoke
    public static UserRemote userRemote;
    public static User user;

    @Test
    public void testSaveUser() {
        user = new User();
        user.setId(1000);
        user.setName("张三");
        Response response = userRemote.saveUser(user);
        System.out.println(JSONObject.toJSONString(response));

        Long start = System.currentTimeMillis();
		for(int i=1;i<1000000;i++){
			userRemote.saveUser(user);
		}
		Long end = System.currentTimeMillis();
		Long count = end-start;
		System.out.println("总计时:"+count/1000+"秒");
    }

    @Test
    public void testSaveUsers() {
        List<User> users = new ArrayList<>();
        user = new User();
        user.setId(1);
        user.setName("张三");
        users.add(user);
        Response response = userRemote.saveUsers(users);
        System.out.println(JSONObject.toJSONString(response));
    }
}
