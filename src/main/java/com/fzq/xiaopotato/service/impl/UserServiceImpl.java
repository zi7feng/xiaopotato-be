package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.JwtUtils;
import com.fzq.xiaopotato.common.PasswordUtils;
import com.fzq.xiaopotato.common.RegexValidator;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.UserMapper;
import com.fzq.xiaopotato.model.dto.UserLoginDTO;
import com.fzq.xiaopotato.model.dto.UserRegisterDTO;
import com.fzq.xiaopotato.model.dto.UserUpdateDTO;
import com.fzq.xiaopotato.model.entity.User;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static com.fzq.xiaopotato.constant.UserConstant.ADMIN_ROLE;
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

    @Autowired
    private JwtUtils jwtUtils;


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

        if (RegexValidator.isNotValidAccount(userAccount)) {
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
    public UserVO userLogin(UserLoginDTO userLoginDTO) {
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

        return getSafeUser(user);

    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        UserVO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "User not logged in.");
        }
        return true;
    }

    @Override
    public UserVO getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 Bearer 前缀
            Claims claims = jwtUtils.getClaimsFromToken(token);
            Long userId = claims.get("id", Long.class);
            User user = userMapper.selectById(userId);
            if (user != null) {
                return getSafeUser(user);
            }
        }
        return null;
    }

    @Override
    public int updateUser(UserUpdateDTO userUpdateDTO, UserVO currentUser, HttpServletRequest request) {
        if (getCurrentUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "User not logged in.");
        }
        Long id = userUpdateDTO.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid ID.");
        }

        // check authority
        // 2.1 admin can update any info, user can only update their info
        if (!isAdmin(currentUser) && !id.equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = this.getById(userUpdateDTO.getId());
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Cannot find user with id: " + userUpdateDTO.getId());
        }

        if (RegexValidator.isValidEmail(userUpdateDTO.getEmail())) {
            oldUser.setEmail(userUpdateDTO.getEmail());
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid Email.");
        }

        if (RegexValidator.isValidPhoneNumber(userUpdateDTO.getPhone())) {
            oldUser.setPhone(userUpdateDTO.getPhone());
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Invalid Phone.");
        }
        oldUser.setGender(userUpdateDTO.getGender());
        oldUser.setUserAvatar(userUpdateDTO.getUserAvatar());
        oldUser.setDescription(userUpdateDTO.getDescription());
        return this.baseMapper.updateById(oldUser);
    }

    @Override
    public boolean isAdmin(UserVO user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return user.getUserRole().equals(ADMIN_ROLE);
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
        safeUser.setDescription(user.getDescription());
        safeUser.setPhone(user.getPhone());
        safeUser.setStatus(user.getStatus());
        return safeUser;
    }
}




