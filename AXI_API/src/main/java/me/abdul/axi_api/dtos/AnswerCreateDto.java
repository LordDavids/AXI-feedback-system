package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerCreateDto {
    private String content;
    private Integer questionId;
}
