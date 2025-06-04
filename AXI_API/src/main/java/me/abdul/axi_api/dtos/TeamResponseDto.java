package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;
import me.abdul.axi_api.entities.Team;

import java.util.List;

@Getter
@Setter
public class TeamResponseDto {
    private int id;
    private String name;
    private List<UserInformationDto> users;
    private int userAmount;

    public TeamResponseDto(int id, String name, List<UserInformationDto> users, int userAmount) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.userAmount = userAmount;
    }

    public TeamResponseDto(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.users = team.getUsers().stream().map(UserInformationDto::new).toList();
        this.userAmount = team.getUsers().size();
    }

    public TeamResponseDto() {
    }
}