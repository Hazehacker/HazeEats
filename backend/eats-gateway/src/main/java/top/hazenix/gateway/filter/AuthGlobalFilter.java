package top.hazenix.gateway.filter;



import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.hazenix.gateway.config.AuthProperties;
import top.hazenix.gateway.util.JwtTool;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final AuthProperties authProperties;
    private final JwtTool jwtTool;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();// spring工具包里面的匹配器
    //【这个类预先没有用@Bean处理，不在IOC容器里 所以要自己new出来】
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取request
        ServerHttpRequest request = exchange.getRequest();
        // 判断是否需要登录拦截
        if(isExcludePath(request.getPath().toString())){
            // 不需要登录拦截, 放行
            return chain.filter(exchange);
        }
        // 如果需要登录拦截，获取token
        String token = null;
        List<String> headers = request.getHeaders().get("authentication");
        if(headers != null && !headers.isEmpty()){
            token = headers.get(0);
        }

        // 校验(解析)token
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
            // 直接抛异常的话，状态码就是500，彰显不出原因是登录错误，所以catch起来，设置状态码为401
        }catch (Exception e){
            // 拦截，设置响应状态码为401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 没有抛异常，说明校验通过
        // 获取用户id，传递用户信息
        String userInfo = userId.toString();
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header("user-info", userInfo))
                .build();
        //提前约定好请求头，才能取出来
        // 放行
        return chain.filter(swe);
    }

    private boolean isExcludePath(String path){
        for (String excludePath : authProperties.getExcludePaths()) {
            if(antPathMatcher.match(excludePath, path)){
                return true;
            }
        }
        return false;
    }
    @Override
    public int getOrder() {
        return 0;//比nettyRouting小就行
    }



}
