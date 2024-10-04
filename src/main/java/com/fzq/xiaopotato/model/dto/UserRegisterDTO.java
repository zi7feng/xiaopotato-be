package com.fzq.xiaopotato.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterDTO implements Serializable {
    private static final long serialVersionUID = 1620524957106567594L;
    @Schema(description = "User's first name", example = "John")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * User Account
     */
    @Schema(description = "User account", example = "johndoe",
            required = true)
    @NotBlank(message = "User account is required")
    @Size(min = 4, message = "The length of the user account cannot be less than 4")
    @Pattern(regexp = "^[^`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]*$",
            message = "The user account cannot contain special characters")
    private String userAccount;

    @Schema(description = "User's password", example = "password123",
            required = true)
    @NotBlank(message = "User password is required")
    @Size(min = 8, message = "The length of the password cannot be less than 8")
    private String userPassword;

    @Schema(description = "Confirm password", example = "password123",
            required = true)
    @NotBlank(message = "Confirmation password is required")
    @Size(min = 8, message = "The length of the confirmation password cannot be less than 8")
    private String checkPassword;

    /**
     * email
     */
    private String email;
    private String phone;

    private String gender;




}
