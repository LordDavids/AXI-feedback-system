package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FormDto {

    private String name;
    private Boolean isActive;
    private Boolean isPublic;
    private Integer categoryId;
    private List<Integer> teamIds;
    private List<Integer> questionIds;

}
