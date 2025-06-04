package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;
import me.abdul.axi_api.entities.User;

@Getter
@Setter
public class RegisterResultDto {
    private String token;
    private User user;


}
