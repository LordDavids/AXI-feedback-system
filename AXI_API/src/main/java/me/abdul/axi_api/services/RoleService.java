package me.abdul.axi_api.services;

import me.abdul.axi_api.entities.Role;
import me.abdul.axi_api.repos.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getRoles() {
        return (List<Role>) roleRepository.findAll();
    }
}
