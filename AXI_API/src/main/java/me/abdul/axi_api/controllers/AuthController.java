package me.abdul.axi_api.controllers;


import jakarta.validation.Valid;
import me.abdul.axi_api.dtos.LoginResultDto;
import me.abdul.axi_api.dtos.LoginUserDto;
import me.abdul.axi_api.dtos.RegisterDto;
import me.abdul.axi_api.dtos.ChangePasswordDto;
import me.abdul.axi_api.dtos.RegisterResultDto;
import me.abdul.axi_api.entities.Auth;
import me.abdul.axi_api.entities.User;
import me.abdul.axi_api.services.AuthService;
import me.abdul.axi_api.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/auth")
@RestController
public class AuthController {
    @Value("${security.jwt.expiration-time}")
    private long expiration;

    final AuthService authService;

    final EmailService emailService;

    public AuthController(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
    }


    @PostMapping("/login")
    public ResponseEntity<Auth> login(@RequestBody LoginUserDto loginUserDto) {
        LoginResultDto loginResultDto = authService.authenticate(loginUserDto);

        ResponseCookie jwtCookie = ResponseCookie.from("auth-token", loginResultDto.getToken())
                .httpOnly(true)
                .secure(true) // Change to true in when getting a valid SSL certificate
                .path("/")
                .maxAge(expiration / 1000)
                .sameSite("None")
                .build();


        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new Auth(loginResultDto));
    }

    @PostMapping("/register")
    public ResponseEntity<Auth> register(@RequestBody @Valid RegisterDto registerDto) {
        RegisterResultDto result = authService.register(registerDto);

        return ResponseEntity.ok()
                .body(new Auth(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie jwtCookie = ResponseCookie.from("auth-token", "")
                .httpOnly(true)
                .secure(true) // Change to true in when getting a valid SSL certificate
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        authService.changePassword(currentUser.getId(), changePasswordDto);
        return ResponseEntity.ok("Password changed successfully");
    }

}
