
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
    public UserRemote userRemote;

    @Test
    public void testSaveUser() {
        User u = new User();
        u.setId(1);
        u.setName("张三");
        Response response = userRemote.saveUser(u);
        System.out.println(JSONObject.toJSONString(response));

        Long start = System.currentTimeMillis();
		for(int i=1;i<100000;i++){
			userRemote.saveUser(u);
		}
		Long end = System.currentTimeMillis();
		Long count = end-start;
		System.out.println("总计时:"+count/1000+"秒");
    }

    @Test
    public void testSaveUsers() {
        List<User> users = new ArrayList<>();
        User u = new User();
        u.setId(1);
        u.setName("张三");
        users.add(u);
        Response response = userRemote.saveUsers(users);
        System.out.println(JSONObject.toJSONString(response));
    }
}
