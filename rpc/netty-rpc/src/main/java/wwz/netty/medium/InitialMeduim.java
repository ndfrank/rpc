package wwz.netty.medium;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import wwz.netty.annotation.Remote;


import java.lang.reflect.Method;
import java.util.Map;

@Component
public class InitialMeduim implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if ((o.getClass().isAnnotationPresent(Remote.class))) {
            Method[] methods = o.getClass().getDeclaredMethods();
            for (Method m : methods) {
                String key = o.getClass().getInterfaces()[0].getName() + "." + m.getName();
                Map<String, BeanMethod> beanMap = Media.beanMap;
                BeanMethod beanMethod = new BeanMethod();
                beanMethod.setBean(o);
                beanMethod.setMethod(m);
                beanMap.put(key, beanMethod);
            }
        }
        return o;
    }
}
