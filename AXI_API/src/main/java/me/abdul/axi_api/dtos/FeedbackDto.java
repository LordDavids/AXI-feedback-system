package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;
import me.abdul.axi_api.entities.Answer;
import java.util.Set;

import java.time.Instant;

@Getter
@Setter
public class FeedbackDto {
    private Integer id;
    private Instant date;
    private Set<Answer> answers;
    private UserInformationDto sender;
    private UserInformationDto receiver;
    private Integer score;
    private Integer teamId;
    private String formName;

    public FeedbackDto(
            Integer id,
            Instant date,
            Set<Answer> answers,
            UserInformationDto sender,
            UserInformationDto receiver,
            Integer score,
            Integer teamId,
            String formName
    ) {
        this.id = id;
        this.date = date;
        this.answers = answers;
        this.sender = sender;
        this.receiver = receiver;
        this.score = score;
        this.teamId = teamId;
        this.formName = formName;
    }
}

