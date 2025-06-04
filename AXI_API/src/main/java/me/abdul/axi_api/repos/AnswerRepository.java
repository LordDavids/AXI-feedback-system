package me.abdul.axi_api.repos;

import me.abdul.axi_api.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    @Query("SELECT a FROM Answer a " +
            "JOIN a.feedback f " +
            "WHERE f.date BETWEEN :startDate AND :endDate " +
            "AND f.team.id = :teamId " +
            "AND f.form.category.id = :formCategoryId " +
            "AND f.receiver.id = :userId")
    List<Answer> findAllByDateRangeAndTeamIdAndUserIdAndCategoryId(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("teamId") Integer teamId,
            @Param("formCategoryId") Integer formCategoryId,
            @Param("userId") Integer userId
    );
}
