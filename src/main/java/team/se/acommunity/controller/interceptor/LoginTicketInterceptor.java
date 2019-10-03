package team.se.acommunity.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import team.se.acommunity.entity.LoginTicket;
import team.se.acommunity.entity.User;
import team.se.acommunity.service.UserService;
import team.se.acommunity.util.CookieUtil;
import team.se.acommunity.util.HostHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 因为登录之后可能随时会用到用户信息，所以要在每一次请求之前就要将User取到，以备后面有可能会用到
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 要获得浏览器端传过来的cookie，cookie是通过reques传过来的
        // 获取ticket
        String ticket = CookieUtil.getValue(request, "ticket");

        // 如果取得ticket，说明用户已经登录
        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.getLoginTicket(ticket);
            // 检查凭证是否有效                                                                 // 表示有效截至时间晚于当前时间，就返回true
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.getUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 将存储在当前线程中的user对象传递给model，这样就可以在界面中使用
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 本次请求结束后要将数据清理掉，线程是一直都在的，可能下一次这个线程就给了别的用户用了，所以在这个用户使用完这个线程之后就要把自己的数据给清除掉
        hostHolder.clear();
    }
}
