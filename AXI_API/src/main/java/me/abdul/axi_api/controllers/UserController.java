package me.abdul.axi_api.controllers;

import me.abdul.axi_api.dtos.UserInformationDto;
import me.abdul.axi_api.entities.User;
import me.abdul.axi_api.exceptions.UserNotFoundException;
import me.abdul.axi_api.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/user")
@RestController
public class UserController {
    final UserService userService;

    public UserController(UserService userservice) {
        this.userService = userservice;
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserInformationDto>> searchUsers(@RequestParam String searchTerm) {
        List<UserInformationDto> userInformationDtos = userService.getUsersBySearchTerm(searchTerm);

        if (userInformationDtos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(userInformationDtos, HttpStatus.OK);
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserInformationDto> changeRoles(@PathVariable Integer id, @RequestBody List<Integer> roles) {
        Optional<User> user = userService.changeRoles(id, roles);

        if(user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        return new ResponseEntity<>(
            new UserInformationDto(user.get()),
            HttpStatus.OK
        );
    }
}