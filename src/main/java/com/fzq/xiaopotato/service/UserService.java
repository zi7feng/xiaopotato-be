package com.fzq.xiaopotato.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.common.PageDTO;
import com.fzq.xiaopotato.model.dto.user.UserLoginDTO;
import com.fzq.xiaopotato.model.dto.user.UserQueryDTO;
import com.fzq.xiaopotato.model.dto.user.UserRegisterDTO;
import com.fzq.xiaopotato.model.dto.user.UserUpdateDTO;
import com.fzq.xiaopotato.model.entity.Post;
import com.fzq.xiaopotato.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fzq.xiaopotato.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author zfeng
* @description 针对表【User(User table)】的数据库操作Service
* @createDate 2024-10-03 18:26:44
*/
public interface UserService extends IService<User> {
    Long userRegister(UserRegisterDTO userRegisterDTO);

    UserVO userLogin(UserLoginDTO userLoginDTO);

    Boolean userLogout(HttpServletRequest request);

    UserVO getCurrentUser(HttpServletRequest request);

    int updateUser(UserUpdateDTO userUpdateDTO, UserVO userVO, HttpServletRequest request);

    boolean isAdmin(UserVO user);

    IPage<Post> listLikesByPage(PageDTO pageDTO, HttpServletRequest request);

    IPage<Post> listSavesByPage(PageDTO pageDTO, HttpServletRequest request);

    UserVO selectUserById(IdDTO idDTO, HttpServletRequest request);

    IPage<UserVO> listUserByPage(UserQueryDTO userQueryDTO, HttpServletRequest request);

    IPage<UserVO> listFansByPage(PageDTO pageDTO, HttpServletRequest request);
    IPage<UserVO> listFollowsByPage(PageDTO pageDTO, HttpServletRequest request);

}
