package com.fzq.xiaopotato.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.common.*;
import com.fzq.xiaopotato.common.utils.JwtUtils;
import com.fzq.xiaopotato.common.utils.ResultUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.common.PageDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.dto.user.UserLoginDTO;
import com.fzq.xiaopotato.model.dto.user.UserQueryDTO;
import com.fzq.xiaopotato.model.dto.user.UserRegisterDTO;
import com.fzq.xiaopotato.model.dto.user.UserUpdateDTO;
import com.fzq.xiaopotato.model.entity.Post;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import com.fzq.xiaopotato.model.vo.PostVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/user")
@Tag(name = "User Controller", description = "Operations related to user management such as registration, login, logout, and fetching the current user.")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * User register
     * @param userRegisterDTO user register request
     * @return BaseResponse<Long>
     */
    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long result = userService.userRegister(userRegisterDTO);
        return ResultUtils.success(result);
    }

    @Operation(summary = "User login")
    @PostMapping("/login")
    public BaseResponse<Map<String, Object>> userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        if (userLoginDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        UserVO user = userService.userLogin(userLoginDTO);

        String token = jwtUtils.generateToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return ResultUtils.success(response);
    }

    @Operation(summary = "Logout the current user")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get current logged-in user details and refresh token")
    @GetMapping("/current")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // get old token, put it into the blacklist
        String oldToken = request.getHeader("Authorization").substring(7);
        jwtUtils.addToBlacklist(oldToken);

        String newToken = jwtUtils.generateToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("token", newToken);
        response.put("user", user);

        return ResultUtils.success(response);
    }

    @Operation(summary = "Update user details")
    @PostMapping("/update")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateDTO userUpdateDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        int result = userService.updateUser(userUpdateDTO, user, request);
        if (result > 0) {
            return ResultUtils.success(true);
        }

        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update fail.");

    }


    @Operation(summary = "Delete User By Id")
    @PostMapping("/deleteById")
    public BaseResponse<Boolean> deleteUser(@RequestBody IdDTO idDTO, HttpServletRequest request) {
        int result = userService.deleteUserById(idDTO, request);
        if (result > 0) {
            return ResultUtils.success(true);
        }

        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "delete fail.");
    }

    @Operation(summary = "Get UserVO by id")
    @GetMapping("/selectById")
    public BaseResponse<UserVO> getUserById(IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id is null");
        }
        return ResultUtils.success(userService.selectUserById(idDTO, request));

    }

    @Operation(summary = "Get Users by page")
    @GetMapping("/selectByPage")
    public BaseResponse<IPage<UserVO>> listUserByPage(UserQueryDTO userQueryDTO, HttpServletRequest request) {
        if (userQueryDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Null query dto.");
        }
        if (userQueryDTO.getCurrentPage() <= 0 || userQueryDTO.getPageSize() <= 0) {
            userQueryDTO.setCurrentPage(1);
            userQueryDTO.setPageSize(10);
        }
        IPage<UserVO> result = userService.listUserByPage(userQueryDTO, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get fans list of current user by page")
    @GetMapping("/fans")
    public BaseResponse<IPage<UserVO>> listFansByPage(PageDTO pageDTO, HttpServletRequest request) {
        if (pageDTO.getCurrentPage() <= 0 || pageDTO.getPageSize() <= 0) {
            pageDTO.setCurrentPage(1);
            pageDTO.setPageSize(10);
        }
        IPage<UserVO> result = userService.listFansByPage(pageDTO, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get current user's following list by page")
    @GetMapping("/follows")
    public BaseResponse<IPage<UserVO>> listFollowsByPage(PageDTO pageDTO, HttpServletRequest request) {
        if (pageDTO.getCurrentPage() <= 0 || pageDTO.getPageSize() <= 0) {
            pageDTO.setCurrentPage(1);
            pageDTO.setPageSize(10);
        }
        IPage<UserVO> result = userService.listFollowsByPage(pageDTO, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get Users by page")
    @GetMapping("/selectNotificationByPage")
    public BaseResponse<IPage<NotificationVO>> listUserByPage(PageDTO pageDTO, HttpServletRequest request) {
        if (pageDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Null query dto.");
        }
        if (pageDTO.getCurrentPage() <= 0 || pageDTO.getPageSize() <= 0) {
            pageDTO.setCurrentPage(1);
            pageDTO.setPageSize(10);
        }
        IPage<NotificationVO> result = userService.listNotificationByPage(pageDTO, request);
        return ResultUtils.success(result);
    }



}
