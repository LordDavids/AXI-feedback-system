package me.abdul.axi_api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class TeamPageDTO {
    private Collection<TeamResponseDto> teams;
    private int totalPages;
    private long totalElements;

    public TeamPageDTO(Collection<TeamResponseDto> teams, int totalPages, long totalElements) {
        this.teams = teams;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
