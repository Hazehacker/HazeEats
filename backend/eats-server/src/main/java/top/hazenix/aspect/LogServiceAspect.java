package top.hazenix.aspect;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 使用AOP切Service打印日志
 *
 * @author Promsing(Hazenix)
 * @version 1.0.0
 * @since 2025/11/23 - 14:56
 */
@Aspect
@Component
@Slf4j
public class LogServiceAspect {
    @Value("${aspect.log-service.enabled}")
    private Boolean onOff;
    @Pointcut("execution(* top.hazenix.service.*.*.*(..))")
    public void logServicePointCut(){}

    @Before("logServicePointCut()")
    public void logBeforeService(JoinPoint joinPoint){
        // 检查开关是否打开
        if (!onOff) {
            return ;
        }
        // 接收到请求，记录请求内容
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes==null){
            return ;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 记录下请求URL、请求方式和路径【可选】
        //(getSignature().getDeclaringTypeName()获取包+类名   joinPoint.getSignature.getName()获取方法名)
        String fullClassName = joinPoint.getSignature().getDeclaringTypeName();
        String shortClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        log.info("URL: {}", request.getRequestURI().toString());
        log.info("({})请求路径： {}.{}", request.getMethod(), shortClassName, joinPoint.getSignature().getName());
        //记录ip
        log.info("IP : " + request.getRemoteAddr());

    }

}
