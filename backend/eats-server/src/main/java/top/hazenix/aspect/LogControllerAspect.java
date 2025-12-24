package top.hazenix.aspect;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 使用AOP切Controller打印日志
 *
 * @author Promsing(Hazenix)
 * @version 1.0.0
 * @since 2025/11/23 - 14:56
 */
@Aspect
@Component
@Slf4j
public class LogControllerAspect {
    //控制是否开启日志
    @Value("${aspect.log-controller.enabled}")
    private Boolean onOff;
    @Pointcut("execution(* top.hazenix.controller.*.*.*(..))")
    public void logControllerPointCut(){}

    @Around("logControllerPointCut()")
    public Object logControllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 检查开关是否打开
        if (!onOff) {
            return joinPoint.proceed();
        }
        
        Object returnValue = null;
        final List<Object> params = new ArrayList<>();

        // 使用 RequestContextHolder 获取当前线程绑定的 HTTP 请求。
        // 如果，则跳过日志
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //当获取不到请求上下文时（不在 Web 环境，比如单元测试或异步任务中调用 Controller） 跳过日志记录，而正常请求则会进入日志记录流程。
        if (sra == null) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = sra.getRequest();
        Object[] args = joinPoint.getArgs();

        //过滤出需要打印的业务参数
        for(int i = 0; i < args.length; i++){
            Object object = args[i];
            // 跳过常见的不用打印的 Web 相关对象
            if (object instanceof HttpServletRequest ||
                    object instanceof HttpServletResponse ||
                    object instanceof MultipartFile ||
                    object instanceof Principal ||
                    object instanceof Locale ||
                    object instanceof TimeZone ||
                    object == null) {
                continue;
            }
            params.add(object);
        }

        //log.info("--------------------请求开始--------------------");
        String cloneParams = null;
        if (log.isInfoEnabled()) {//如果没有开启info级别，就不进行序列化，提高性能
            cloneParams = JSONObject.toJSONString(params);
        }
        String fullClassName = joinPoint.getSignature().getDeclaringTypeName();
        String shortClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        log.info("({})请求路径： {}.{} \n请求参数： {}", request.getMethod(), shortClassName, joinPoint.getSignature().getName(), cloneParams);

        long startTime = System.currentTimeMillis();
        //执行方法
        returnValue = joinPoint.proceed(joinPoint.getArgs());

        //处理返回值，计算时间
        long endTime = System.currentTimeMillis();

        //如果返回值不为空就打印响应结果
        if(returnValue != null){
            log.info("请求用时： {} 用时 {} ms \n响应结果： {}", request.getRequestURI(), endTime - startTime, JSONObject.toJSONString(returnValue));

        }else{
            log.info("请求用时： {} 用时 {} ms", request.getRequestURI(), endTime - startTime);
        }
//        log.info("--------------------请求结束--------------------");
        return returnValue;
    }
}