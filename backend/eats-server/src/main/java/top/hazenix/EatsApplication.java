package top.hazenix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching//开启缓存注解功能
@EnableAspectJAutoProxy // 启用AspectJ自动代理
public class EatsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EatsApplication.class, args);
        log.info("server started");
    }
}