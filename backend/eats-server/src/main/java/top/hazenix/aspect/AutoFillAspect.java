package top.hazenix.aspect;


import top.hazenix.annotation.AutoFill;
import top.hazenix.context.BaseContext;
import top.hazenix.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，完成公共功能的处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* top.hazenix.mapper.*.*(..)) && @annotation(top.hazenix.annotation.AutoFill)")
    public void  autoFillPointCut(){}

    /**
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段的填充");
        //获取当前拦截到的方法的数据库操作类型是什么（update还是insert[value = xx时设置了]）
        Signature signature = (MethodSignature)(joinPoint.getSignature());//方法签名对象
        AutoFill autoFill = ((MethodSignature) signature).getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到当前拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();//这里约定即时方法有多个参数，实体对象也放在第一个
        if(args == null){
            return;//做个保险
        }
        Object entity = args[0];
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            try{
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime",LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser",Long.class);
                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);

            }catch(Exception e){
                e.printStackTrace();
            }

        }


        try{
            Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime",LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser",Long.class);
            //通过反射为对象属性赋值
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentId);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
