package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeedbackCreateDto {
    private Integer receiverId;
    private Integer score;
    private Integer formId;

    private List<AnswerCreateDto> answers;
}
