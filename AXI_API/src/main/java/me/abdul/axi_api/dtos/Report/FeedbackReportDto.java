package me.abdul.axi_api.dtos.Report;

import lombok.Getter;
import lombok.Setter;
import me.abdul.axi_api.dtos.UserInformationDto;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class FeedbackReportDto {
    private Instant date;
    private UserInformationDto sender;
    private Integer score;
    private List<AnswerDto> answers;
}
