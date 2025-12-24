package top.hazenix.service;

import top.hazenix.dto.UserLoginDTO;
import top.hazenix.entity.User;

public interface UserService {
    User login(UserLoginDTO userLoginDTO);
}
