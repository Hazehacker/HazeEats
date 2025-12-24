package top.hazenix.shop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching//开启缓存注解功能
//@EnableFeignClients(basePackages = "top.hazenix.api.client") // 启用Feign客户端，扫描指定包下的Feign接口
@ComponentScan(basePackages = {"top.hazenix", "top.hazenix.utils"}) // 扫描指定包，包括工具类所在的包
public class ShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
        log.info("server started");
    }
}