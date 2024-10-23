package com.fzq.xiaopotato.controller;

import com.fzq.xiaopotato.common.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
                                                """))),
            @ApiResponse(responseCode = "40000", description = "Request parameter error",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                                {
                                                  "code": 40000,
                                                  "data": null,
                                                  "message": "Request parameter error",
                                                  "description": ""
                                                }
                                                """))),
            @ApiResponse(responseCode = "40001", description = "Request data null",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                                {
                                                  "code": 40001,
                                                  "data": null,
                                                  "message": "Request data null",
                                                  "description": ""
                                                }
                                                """))),
            @ApiResponse(responseCode = "50000", description = "System error",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                                {
                                                  "code": 50000,
                                                  "data": null,
                                                  "message": "system error",
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
                                                """))),
            @ApiResponse(responseCode = "40100", description = "Not logged in",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                                {
                                                  "code": 40100,
                                                  "data": null,
                                                  "message": "not login",
                                                  "description": ""
                                                }
                                                """))),
            @ApiResponse(responseCode = "40301", description = "Forbid to operate",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                                {
                                                  "code": 40301,
                                                  "data": null,
                                                  "message": "forbid to operate",
                                                  "description": ""
                                                }
                                                """))),
            @ApiResponse(responseCode = "50000", description = "System error",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                                {
                                                  "code": 50000,
                                                  "data": null,
                                                  "message": "system error",
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

    @Operation(summary = "Get current logged-in user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user details",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                                {
                                                  "code": 200,
                                                  "data": {
                                                    "id": 123,
                                                    "firstName": "John",
                                                    "lastName": "Doe",
                                                    "email": "john.doe@example.com",
                                                    "userAccount": "johndoe"
                                                  },
                                                  "message": "ok",
                                                  "description": ""
                                                }
                                                """))),
            @ApiResponse(responseCode = "40100", description = "Not logged in",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                                {
                                                  "code": 40100,
                                                  "data": null,
                                                  "message": "not login",
                                                  "description": ""
                                                }
                                                """))),
            @ApiResponse(responseCode = "50000", description = "System error",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                                {
                                                  "code": 50000,
                                                  "data": null,
                                                  "message": "system error",
                                                  "description": ""
                                                }
                                                """)))
    })
    @GetMapping("/current")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        return ResultUtils.success(user);
    }


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


    @PostMapping("/upload")
    public BaseResponse<String> upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "File is null.");
        }
        return ResultUtils.success(UploadUtils.uploadImage(file));
    }
}
