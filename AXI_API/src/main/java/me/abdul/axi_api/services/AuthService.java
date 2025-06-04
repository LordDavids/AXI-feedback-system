package me.abdul.axi_api.services;

import me.abdul.axi_api.dtos.LoginResultDto;
import me.abdul.axi_api.dtos.LoginUserDto;
import me.abdul.axi_api.dtos.RegisterDto;
import me.abdul.axi_api.dtos.RegisterResultDto;
import me.abdul.axi_api.dtos.ChangePasswordDto;
import me.abdul.axi_api.entities.Role;
import me.abdul.axi_api.entities.User;
import me.abdul.axi_api.exceptions.IncorrectPasswordException;
import me.abdul.axi_api.exceptions.RoleNotFoundException;
import me.abdul.axi_api.exceptions.UserAlreadyExistsException;
import me.abdul.axi_api.exceptions.invalidCredentialsException;
import me.abdul.axi_api.repos.RoleRepository;
import me.abdul.axi_api.repos.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;



@Service
public class AuthService {

    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;
    private final RoleRepository roleRepository;
    @Value("${password.pepper}")
    private String pepper;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, LocalContainerEntityManagerFactoryBean entityManagerFactory, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder(10);
        this.entityManagerFactory = entityManagerFactory;
        this.roleRepository = roleRepository;
    }

    public RegisterResultDto register(RegisterDto input) {
        try {
            // Create a new user
            User user = new User();
            user.setFirstName(input.getFirstName());
            user.setInfix(input.getInfix());
            user.setLastName(input.getLastName());
            user.setEmail(input.getEmail());
            user.setPassword(passwordEncoder.encode((input.getPassword() + pepper)));

            List<Role> roles = new ArrayList<>();
            for(Integer roleId : input.getRoles()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new RoleNotFoundException("Role does not exist"));
                roles.add(role);
            }

            user.setRoles(roles);

            // Save the user in the repository
            User newUser = userRepository.save(user);

            // Generate JWT token
            String token = jwtService.generateToken(newUser);

            // Prepare the response
            RegisterResultDto registerResultDto = new RegisterResultDto();
            registerResultDto.setToken(token);
            registerResultDto.setUser(newUser);

            return registerResultDto;
        } catch (DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException cause) {
                if(cause.getMessage().contains("Duplicate entry")){
                    throw new UserAlreadyExistsException("User with this email already exists");
                }

                if(cause.getMessage().contains("user_role_role_role_id_fk")) {
                    throw new RoleNotFoundException("Role does not exist");
                }
            }
            throw e;
        }
    }

    public LoginResultDto authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new invalidCredentialsException("invalid credentials"));

        if (passwordEncoder.matches((input.getPassword() + pepper), user.getPassword())) {
            String token = jwtService.generateToken(user);
            LoginResultDto loginResultDto = new LoginResultDto();
            loginResultDto.setToken(token);
            loginResultDto.setUser(user);
            return loginResultDto;
        }

        throw new invalidCredentialsException("invalid credentials");
    }

    public void changePassword(int userId, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new invalidCredentialsException("User not found"));

        // Check if the old password is correct
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword() + pepper, user.getPassword())) {
            throw new IncorrectPasswordException("Invalid password");
        }

        // Encode the new password and update the user
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword() + pepper));
        userRepository.saveAndFlush(user);
    }
}
