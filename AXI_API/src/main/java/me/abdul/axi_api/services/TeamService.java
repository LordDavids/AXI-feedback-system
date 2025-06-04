package me.abdul.axi_api.services;

import jakarta.persistence.EntityNotFoundException;
import me.abdul.axi_api.dtos.*;
import me.abdul.axi_api.entities.Role;
import me.abdul.axi_api.entities.Team;
import me.abdul.axi_api.entities.User;
import me.abdul.axi_api.exceptions.AccessDeniedException;
import me.abdul.axi_api.exceptions.TeamNotFoundException;
import me.abdul.axi_api.repos.TeamRepository;
import me.abdul.axi_api.repos.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public TeamResponseDto addTeam(TeamCreateDto teamCreateDto) {
        Team team = new Team();

        List<Integer> userIds = teamCreateDto.getUsers().stream().distinct().toList();
        List<User> users = userIds.isEmpty()
                ? new ArrayList<>()
                : (List<User>) userRepository.findAllById(userIds);

        team.setName(teamCreateDto.getTeamName());
        team.setUsers(users);

        teamRepository.save(team);

        TeamResponseDto teamResponseDto = new TeamResponseDto();
        teamResponseDto.setId(team.getId());

        return teamResponseDto;
    }

    public TeamResponseDto editTeam(TeamEditDto teamEditDto) {
        Team team = teamRepository.findTeamById(teamEditDto.getId());

        if(teamEditDto.getTeamName() != null && !teamEditDto.getTeamName().isBlank()) {
            team.setName(teamEditDto.getTeamName());
        }

        List<Integer> userIds = teamEditDto.getUsers().stream().distinct().toList();
        List<User> users = userIds.isEmpty()
                ? new ArrayList<>()
                : (List<User>) userRepository.findAllById(userIds);

        team.setUsers(users);

        teamRepository.saveAndFlush(team);

        TeamResponseDto teamResponseDto = new TeamResponseDto();
        teamResponseDto.setId(team.getId());

        return teamResponseDto;
    }

    public TeamPageDTO getTeamsByUser(User user, int page, int size) {
        boolean isManager = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("manager"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Team> teams;

        if (isManager) {
            teams = teamRepository.findAllActive(user.getId(), pageable);
        }
        else {
            teams = teamRepository.findTeamsByUserId(user.getId(), pageable);
        }
        List<TeamResponseDto> teamResponseDtos = new ArrayList<>();

        for (Team team : teams) {
            TeamResponseDto teamResponseDto = new TeamResponseDto(team);
            teamResponseDtos.add(teamResponseDto);
        }
        return new TeamPageDTO(teamResponseDtos, teams.getTotalPages(), teams.getTotalElements());
    }

    public TeamResponseDto getTeamById(User user, int id) {
        boolean isManager = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("manager"));

        Team team = teamRepository.findTeamById(id);
        if (team == null) {
            throw new TeamNotFoundException("Team with id " + id + " not found");
        }
        if (!isManager) {
            boolean isUserInTeam = team.getUsers().stream()
                    .anyMatch(member -> member.getId().intValue() == user.getId().intValue());

            if (!isUserInTeam) {
                throw new AccessDeniedException("Access denied: User is not a member of this team");
            }
        }

        return new TeamResponseDto(team);
    }

    public TeamPageDTO getTeamsByName(User user, int page, int size, String name){
        boolean isManager = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("manager"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Team> teams;

        if (isManager) {
            teams = teamRepository.findTeamsByName(user.getId(), name, pageable);
        }
        else {
            teams = teamRepository.findTeamsByUserIdAndName(user.getId(), name, pageable);
        }

        List<TeamResponseDto> teamResponseDtos = new ArrayList<>();

        for (Team team : teams) {
            TeamResponseDto teamResponseDto = new TeamResponseDto(team);
            teamResponseDtos.add(teamResponseDto);
        }

        return new TeamPageDTO(teamResponseDtos, teams.getTotalPages(), teams.getTotalElements());
    }
    public void deactivateTeamById(Integer id) {
        Team team = teamRepository.findTeamById(id);
        team.setActive(false);
        teamRepository.saveAndFlush(team);
    }
}
