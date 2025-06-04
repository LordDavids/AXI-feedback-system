package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;
import me.abdul.axi_api.entities.Role;
import me.abdul.axi_api.entities.User;

@Getter
@Setter
public class UserInformationDto {
    private int id;
    private String firstName;
    private String infix;
    private String lastName;
    private String email;
    private String[] roles;

    public UserInformationDto(int id, String firstName, String infix, String lastName, String email, String[] roles) {
        this.id = id;
        this.firstName = firstName;
        this.infix = infix;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }

    public UserInformationDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.infix = user.getInfix();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream().map(Role::getRoleName).toArray(String[]::new);
    }

    public UserInformationDto() {
    }
}
