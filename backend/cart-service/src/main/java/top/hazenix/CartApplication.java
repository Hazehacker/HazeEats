package top.hazenix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching//开启缓存注解功能
@EnableAspectJAutoProxy // 启用AspectJ自动代理
@EnableFeignClients(basePackages = "top.hazenix.api.client") // 启用Feign客户端，扫描指定包下的Feign接口
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
        log.info("server started");
    }
}