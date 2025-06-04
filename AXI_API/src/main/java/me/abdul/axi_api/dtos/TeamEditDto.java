package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamEditDto {
    Integer id;
    String teamName;
    List<Integer> users;
}
