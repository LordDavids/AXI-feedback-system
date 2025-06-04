package me.abdul.axi_api.repos;


import me.abdul.axi_api.entities.Form;
import me.abdul.axi_api.entities.FormCategory;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Integer> {



    Optional<List<Form>> findByTeamIds(Integer teamId);

    Optional<List<Form>> findAllByIsPublic(Boolean isPublic);

    Optional<List<Form>> findAllByCategory(FormCategory category);

    Optional<List<Form>> findAllByIsActiveAndTeamIds(Boolean isActive, Integer teamId);

}
