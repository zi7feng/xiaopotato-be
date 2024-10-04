package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.PasswordUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.UserMapper;
import com.fzq.xiaopotato.model.dto.UserLoginDTO;
import com.fzq.xiaopotato.model.dto.UserRegisterDTO;
import com.fzq.xiaopotato.model.entity.User;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fzq.xiaopotato.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author zfeng
* @description 针对表【User(User table)】的数据库操作Service实现
* @createDate 2024-10-03 18:26:44
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    private static final String SALT = "potato";

    @Autowired
    private UserMapper userMapper;


    @Override
    public Long userRegister(UserRegisterDTO userRegisterDTO) {
        String firstName = userRegisterDTO.getFirstName();
        String lastName = userRegisterDTO.getLastName();
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String email = userRegisterDTO.getEmail();
        String gender= userRegisterDTO.getGender();
        String phone = userRegisterDTO.getPhone();

        String checkPassword = userRegisterDTO.getCheckPassword();

        if (StringUtils.isAnyBlank(firstName, lastName, userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The length of the user account cannot be less than 4");
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The length of the password cannot be less than 8");
        }

        // no special characters
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);

        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The user account cannot contain special characters");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The two passwords are not matched");
        }


        // duplicate account
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account already exists.");
        }

        // encrypt
        String encryptPassword = PasswordUtils.encryptPassword(userPassword, SALT);        // is account exist
        User user = new User();
        user.setUserAccount(userAccount);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserPassword(encryptPassword);
        user.setEmail(email);
        user.setGender(gender);
        user.setPhone(phone);
        boolean saveResult = this.save(user);
        if (saveResult) {
            return user.getId();
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Register failed.");
        }
    }

    @Override
    public UserVO userLogin(UserLoginDTO userLoginDTO, HttpServletRequest request) {
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // encrypt
        String encryptPassword = PasswordUtils.encryptPassword(userPassword, SALT);        // is account exist
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // doesn't exist
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Wrong account or password.");
        }
        UserVO safeUser = getSafeUser(user);
        // 4. set login state
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, safeUser);
        session.setMaxInactiveInterval(60 * 60); // expire time: 60 * 60s

        return safeUser;

    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getCurrentUser(HttpServletRequest request) {
        UserVO user = (UserVO) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return user;
    }

    private UserVO getSafeUser(User user) {
        UserVO safeUser = new UserVO();
        safeUser.setFirstName(user.getFirstName());
        safeUser.setLastName(user.getLastName());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setEmail(user.getEmail());
        safeUser.setId(user.getId());
        safeUser.setUserAvatar(user.getUserAvatar());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setStatus(user.getStatus());
        return safeUser;
    }
}




