package me.abdul.axi_api.services;

import me.abdul.axi_api.dtos.UserInformationDto;
import me.abdul.axi_api.dtos.UserSearchDto;
import me.abdul.axi_api.entities.Role;
import me.abdul.axi_api.entities.User;
import me.abdul.axi_api.exceptions.RoleNotFoundException;
import me.abdul.axi_api.repos.RoleRepository;
import me.abdul.axi_api.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final RoleRepository roleRepository;
    private UserRepository userRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserInformationDto> getUsersBySearchTerm(String searchTerm) {
        Optional<List<User>> users = userRepository.findUsersByEmailContaining(searchTerm);

        List<UserInformationDto> userInformationDtos = new ArrayList<>();
        if (users.isPresent()) {
            for (User user : users.get()) {
                UserInformationDto userInformationDto = new UserInformationDto();
                userInformationDto.setId(user.getId());
                userInformationDto.setFirstName(user.getFirstName());
                userInformationDto.setInfix(user.getInfix());
                userInformationDto.setLastName(user.getLastName());
                userInformationDto.setEmail(user.getEmail());
                userInformationDtos.add(userInformationDto);

                Collection<Role> rolesCollection = user.getRoles();

                String[] rolesArray = rolesCollection.stream()
                        .map(Role::getRoleName)
                        .toArray(String[]::new);

                userInformationDto.setRoles(rolesArray);
            }
        }

        return userInformationDtos;
    }

    public Optional<User> changeRoles(Integer id, List<Integer> roles) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            Collection<Role> rolesCollection = new ArrayList<>();
            for (int roleId : roles) {
                Optional<Role> role = roleRepository.findById(roleId);
                if(role.isEmpty()) {
                    throw new RoleNotFoundException("Role does not exist");
                }
                rolesCollection.add(role.get());
            }

            user.get().setRoles(rolesCollection);
            return Optional.of(userRepository.save(user.get()));
        }

        return user;
    }
}
