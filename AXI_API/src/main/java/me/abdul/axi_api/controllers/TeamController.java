package me.abdul.axi_api.controllers;


import me.abdul.axi_api.dtos.TeamCreateDto;
import me.abdul.axi_api.dtos.TeamEditDto;
import me.abdul.axi_api.dtos.TeamPageDTO;
import me.abdul.axi_api.dtos.TeamResponseDto;
import me.abdul.axi_api.entities.Team;
import me.abdul.axi_api.entities.User;
import me.abdul.axi_api.services.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/team")
@RestController
public class TeamController {
    final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/create")
    public ResponseEntity<TeamResponseDto> createTeam(@RequestBody TeamCreateDto teamCreateDto) {
        TeamResponseDto teamResponseDto = teamService.addTeam(teamCreateDto);
        return new ResponseEntity<>(teamResponseDto, HttpStatus.CREATED);
    }

    @GetMapping({ "/{id}" })
    public ResponseEntity<?> getTeam(@PathVariable Integer id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TeamResponseDto teamResponseDto = teamService.getTeamById(user, id);
        return new ResponseEntity<>(teamResponseDto, HttpStatus.OK);
    }

    @PostMapping("/{id}/edit")
    public ResponseEntity<TeamResponseDto> editTeam(@PathVariable Integer id, @RequestBody TeamEditDto teamEditDto) {
        teamEditDto.setId(id);

        TeamResponseDto teamResponseDto = teamService.editTeam(teamEditDto);
        return new ResponseEntity<>(teamResponseDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<TeamPageDTO> getTeams(@RequestParam Integer page, @RequestParam Integer size, @RequestParam(required = false) String name) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TeamPageDTO teamPageDTO;

        if(name == null || name.isBlank()) {
            teamPageDTO = teamService.getTeamsByUser(user, page, size);
        } else {
            teamPageDTO = teamService.getTeamsByName(user, page, size, name);
        }

        return new ResponseEntity<>(teamPageDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteTeam(@PathVariable Integer id) {
        teamService.deactivateTeamById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}