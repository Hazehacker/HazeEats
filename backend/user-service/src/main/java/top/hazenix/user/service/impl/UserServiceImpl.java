package top.hazenix.user.service.impl;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hazenix.constant.MessageConstant;

import top.hazenix.exception.LoginFailedException;

import top.hazenix.properties.WeChatProperties;

import top.hazenix.user.domain.dto.UserLoginDTO;
import top.hazenix.user.domain.entity.User;
import top.hazenix.user.mapper.UserMapper;
import top.hazenix.user.service.UserService;
import top.hazenix.utils.HttpClientUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO.getCode();
        //调用微信接口服务，获得当前用户的openId

        String openid = getOpenid(code);

        //如果openid为空，说明登录失败，抛出业务异常
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        User user = userMapper.selectByOpenid(openid);
        if(user == null){
            //尚未注册
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
            return user;
        }


        return user;
    }

    @Override
    public Integer sumByDateTime(LocalDateTime beginTime, LocalDateTime endTime) {
        return userMapper.sumByDateTime(beginTime,endTime);
    }

    /**
     * 调用微信接口服务，获取微信用户的openid
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        Map<String,String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN,map);
        //获取JSONObject对象
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }



}
