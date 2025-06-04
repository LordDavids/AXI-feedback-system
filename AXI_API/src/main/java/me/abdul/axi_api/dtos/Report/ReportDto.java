package me.abdul.axi_api.dtos.Report;

import lombok.Getter;
import lombok.Setter;
import me.abdul.axi_api.dtos.TeamResponseDto;
import me.abdul.axi_api.dtos.UserInformationDto;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ReportDto {
    private FormCategoryDto category;
    private UserInformationDto Recipient;
    private List<FormReportDto> forms;
    private TeamResponseDto team;
    private Date StartDate;
    private Date EndDate;


}
