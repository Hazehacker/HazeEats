package top.hazenix.user.service;


import top.hazenix.user.domain.dto.UserLoginDTO;
import top.hazenix.user.domain.entity.User;

import java.time.LocalDateTime;

public interface UserService {
    User login(UserLoginDTO userLoginDTO);

    Integer sumByDateTime(LocalDateTime beginTime, LocalDateTime endTime);
}
