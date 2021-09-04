package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;


public class ProxyCglib {
    public static void main(String[] args) {

        /*PersonService proxy = (PersonService) Enhancer.create(PersonService.class, (FixedValue) () -> "Hello Tom!");

        String res = proxy.sayHello(null);

        System.out.println("res: " + res);*/

        MethodInterceptor handler1 = ((obj, method, argts, proxy) -> {
            if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                return "Hello Tom!";
            } else {
                return proxy.invokeSuper(obj, argts);
            }
        });

        User user = new User("Вася");

        MethodInterceptor handler2 = (obj, method, vars, proxy) -> {
            if(method.getName().equals("getName")){
                return ((String)proxy.invoke(user, vars)).toUpperCase() ;
            }
            return proxy.invoke(user, vars);
        };

        MethodInterceptor handler3 = (obj, method, vars, proxy) -> {
            if(method.getName().equals("getName")){
                return ((String)proxy.invokeSuper(proxy, vars)).toUpperCase() ;
            }
            return proxy.invokeSuper(proxy, vars);
        };

        PersonService proxy1 = (PersonService) Enhancer.create(PersonService.class, handler1);
        User proxy2 = (User) Enhancer.create(User.class, handler2);
        User proxy3 = (User) Enhancer.create(User.class, handler3);

        System.out.println("res: " + proxy1.sayHello(null));
        System.out.println("res: " + proxy1.lengthOfName("Mary"));
        System.out.println("res: " + proxy2.getName());

       /* User user = new User("Вася");

        MethodInterceptor handler = (obj, method ,  args,  proxy) -> {
            if(method.getName().equals("getName")){
                return ((String)proxy.invoke(user, args)).toUpperCase() ;
            }
            return proxy.invoke(user, args);
        };

        MethodInterceptor handler = (obj, method ,  args,  proxy) -> {
            if(method.getName().equals("getName")){
                return ((String)proxy.invokeSuper(proxy, args)).toUpperCase() ;
            }
            return proxy.invokeSuper(proxy, args);
        };

        User userProxy = (User) Enhancer.create(User.class, handler);
        assertEquals("ВАСЯ", userProxy.getName());*/
    }

}

@AllArgsConstructor
@Data
class PersonService {
    public String sayHello(String name) {
        return "Hello " + name;
    }

    public Integer lengthOfName(String name) {
        return name.length();
    }
}

class User implements IUser {
    private final String name;

    public User() {
        this(null);
    }

    public User(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}

interface IUser {
    String getName();
}