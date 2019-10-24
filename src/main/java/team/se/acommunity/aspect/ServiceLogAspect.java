package team.se.acommunity.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* team.se.acommunity.service.*.*(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户{1.2.3.4}，在{xxx}，访问了{team.se.acommunity.service.xxx()}
        // 从request对象中获取用户ip
        // 获取request对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 获取ip
        String ip = request.getRemoteHost();
        // 获取请求时间，即当前时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 第一个是拼接的是连接点的类型名      第二个拼接的是连接点的类中的方法名
        String target = joinPoint.getSignature().getDeclaringType() + "." + joinPoint.getSignature().getName();
        // 将日志格式化输出
        logger.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));
    }
}
