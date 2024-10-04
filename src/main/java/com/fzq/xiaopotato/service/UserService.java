package com.fzq.xiaopotato.service;

import com.fzq.xiaopotato.model.dto.UserLoginDTO;
import com.fzq.xiaopotato.model.dto.UserRegisterDTO;
import com.fzq.xiaopotato.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fzq.xiaopotato.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
* @author zfeng
* @description 针对表【User(User table)】的数据库操作Service
* @createDate 2024-10-03 18:26:44
*/
public interface UserService extends IService<User> {
    Long userRegister(UserRegisterDTO userRegisterDTO);

    UserVO userLogin(UserLoginDTO userLoginDTO, HttpServletRequest request);

    Boolean userLogout(HttpServletRequest request);

    UserVO getCurrentUser(HttpServletRequest request);
}
