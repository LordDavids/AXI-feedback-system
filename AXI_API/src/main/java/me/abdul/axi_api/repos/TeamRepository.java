package me.abdul.axi_api.repos;

import me.abdul.axi_api.entities.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    @Query("SELECT t FROM Team t JOIN t.users u WHERE u.id = :userId")
    Page<Team> findTeamsByUserId(int userId, Pageable pageable);

    @Query("SELECT t FROM Team t WHERE t.id = :id AND t.active = true")
    Team findTeamById(int id);

    @Query("SELECT t FROM Team t LEFT JOIN t.users u ON u.id = :userId WHERE t.name LIKE %:name% ORDER BY CASE WHEN u.id IS NOT NULL THEN 0 ELSE 1 END, t.name")
    Page<Team> findTeamsByName(int userId, String name, Pageable pageable);

    @Query("SELECT t FROM Team t JOIN t.users u WHERE u.id = :userId AND t.name LIKE %:name%")
    Page<Team> findTeamsByUserIdAndName(int userId, String name, Pageable pageable);

    @Query("SELECT t FROM Team t LEFT JOIN t.users u ON u.id = :userId ORDER BY CASE WHEN u.id IS NOT NULL THEN 0 ELSE 1 END, t.name")
    Page<Team> findAllTeams(int userId, Pageable pageable);

    @Query("SELECT t FROM Team t LEFT JOIN t.users u ON u.id = :userId WHERE t.active = true ORDER BY CASE WHEN u.id IS NOT NULL THEN 0 ELSE 1 END ")
    Page<Team> findAllActive(int userId, Pageable pageable);
}