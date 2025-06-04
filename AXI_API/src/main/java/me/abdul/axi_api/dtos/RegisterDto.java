package me.abdul.axi_api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import me.abdul.axi_api.annotations.Password;

import java.util.Collection;


@Getter
@Setter
public class RegisterDto {

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name can only contain letters")
    private String firstName;

    private String infix;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name can only contain letters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Password
    private String password;

    private Collection<Integer> roles;
}