package com.fzq.xiaopotato.controller;

import com.fzq.xiaopotato.common.*;
import com.fzq.xiaopotato.common.utils.JwtUtils;
import com.fzq.xiaopotato.common.utils.ResultUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.user.UserLoginDTO;
import com.fzq.xiaopotato.model.dto.user.UserRegisterDTO;
import com.fzq.xiaopotato.model.dto.user.UserUpdateDTO;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "data": 123,
                      "message": "ok",
                      "description": ""
                    }
                """)))
    })
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long result = userService.userRegister(userRegisterDTO);
        return ResultUtils.success(result);
    }

    @Operation(summary = "User login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "code": 200,
                  "data": {
                    "user": {
                      "id": 1,
                      "firstName": "John",
                      "lastName": "Doe",
                      "userAccount": "John",
                      "userAvatar": "string",
                      "email": "string@abc.com",
                      "phone": "123-123-1234",
                      "gender": "string",
                      "description": "#tag",
                      "userRole": "user",
                      "status": 0
                    },
                    "token": "eyJiJ1c2VyIiwiaWF0IjoxNzI5ODY3OTg3LCJleHAiOjE3M"
                  },
                  "message": "ok",
                  "description": ""
                }
            """)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "data": true,
                      "message": "ok",
                      "description": ""
                    }
                """)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get current logged-in user details and refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user details and refreshed token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "code": 200,
                  "data": {
                    "user": {
                      "id": 1,
                      "firstName": "John",
                      "lastName": "Doe",
                      "userAccount": "John",
                      "userAvatar": "string",
                      "email": "string@abc.com",
                      "phone": "123-123-1234",
                      "gender": "string",
                      "description": "#tag",
                      "userRole": "user",
                      "status": 0
                    },
                    "token": "ey9x7IXinZ8pEZ-kD8X4YiYTiuTawWuuKlARlvWY"
                  },
                  "message": "ok",
                  "description": ""
                }
            """)))
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "code": 200,
                      "data": true,
                      "message": "ok",
                      "description": ""
                    }
                """)))
    })
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

}
