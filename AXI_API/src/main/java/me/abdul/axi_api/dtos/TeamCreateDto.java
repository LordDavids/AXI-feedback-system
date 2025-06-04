package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TeamCreateDto {
    String teamName;
    List<Integer> users;
}
