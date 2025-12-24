package top.hazenix.user.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.hazenix.constant.JwtClaimsConstant;

import top.hazenix.properties.JwtProperties;
import top.hazenix.result.Result;

import top.hazenix.user.domain.dto.UserLoginDTO;
import top.hazenix.user.domain.entity.User;
import top.hazenix.user.domain.vo.UserLoginVO;
import top.hazenix.user.service.UserService;
import top.hazenix.utils.JwtUtil;


import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping("/user/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("用户微信登录:{}",userLoginDTO);
        User user = userService.login(userLoginDTO);
        HashMap<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        try {
            return Result.success(userLoginVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 统计指定时间区间内用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    @PostMapping("/sum")
    public Integer sumByDateTime(LocalDateTime beginTime, LocalDateTime endTime){
        return userService.sumByDateTime(beginTime,endTime);
    }


}
