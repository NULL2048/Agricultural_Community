package team.se.acommunity.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import team.se.acommunity.annotation.LoginRequired;
import team.se.acommunity.util.HostHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // HandlerMethod是spring MVC中提供的一个类型，表示如果handler拦截的是一个方法，那么他的类型就应该是HandlerMethod
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获得要访问的方法对象
            Method method = handlerMethod.getMethod();
            // 获得这个方法的指定注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // 取到了loginRequired表明访问这个方法需要登陆，但是hostHolder取不到User说明没有登陆，这就说明出现了问题
            if (loginRequired != null && hostHolder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
