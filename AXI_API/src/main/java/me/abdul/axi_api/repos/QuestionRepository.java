package me.abdul.axi_api.repos;

import me.abdul.axi_api.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    
    List<Question> findAllByIsPublicTrue();

    List<Question> findAllByQuestionContainingAndIsPublicIsTrue(String query);


}
