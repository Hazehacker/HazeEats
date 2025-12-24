package top.hazenix.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.hazenix.user.domain.entity.User;


import java.time.LocalDateTime;

@Mapper
public interface UserMapper {


    void insert(User user);

    User selectByOpenid(String openid);


    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 统计指定时间区间内用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer sumByDateTime(LocalDateTime beginTime, LocalDateTime endTime);
}
