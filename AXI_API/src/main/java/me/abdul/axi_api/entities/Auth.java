package me.abdul.axi_api.entities;

import lombok.Getter;
import lombok.Setter;
import me.abdul.axi_api.dtos.LoginResultDto;
import me.abdul.axi_api.dtos.RegisterResultDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Auth {
    private String firstName;
    private String infix;
    private String lastName;
    private String email;
    private List<String> roles;

    public Auth(LoginResultDto loginResultDto) {
        this.firstName = loginResultDto.getUser().getFirstName();
        this.infix = loginResultDto.getUser().getInfix();
        this.lastName = loginResultDto.getUser().getLastName();
        this.email = loginResultDto.getUser().getEmail();

        this.roles = new ArrayList<>();
        for(Role role : loginResultDto.getUser().getRoles()) {
            roles.add(role.getRoleName());
        }
    }

    public Auth(RegisterResultDto registerResultDto) {
        this.firstName = registerResultDto.getUser().getFirstName();
        this.infix = registerResultDto.getUser().getInfix();
        this.lastName = registerResultDto.getUser().getLastName();
        this.email = registerResultDto.getUser().getEmail();

        this.roles = new ArrayList<>();
        for(Role role : registerResultDto.getUser().getRoles()) {
            roles.add(role.getRoleName());
        }
    }
}
