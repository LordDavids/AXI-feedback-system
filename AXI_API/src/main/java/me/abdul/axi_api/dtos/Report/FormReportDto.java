package me.abdul.axi_api.dtos.Report;

import lombok.Getter;
import lombok.Setter;
import me.abdul.axi_api.dtos.QuestionDto;

import java.util.List;

@Getter
@Setter
public class FormReportDto {
    private String name;
    private List<FormQuestionDto> questions;
    private List<FeedbackReportDto> feedbacks;
}