package me.abdul.axi_api.repos;


import me.abdul.axi_api.entities.FormCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<FormCategory, Integer> {

}
