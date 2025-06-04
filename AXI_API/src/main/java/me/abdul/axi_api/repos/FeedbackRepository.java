package me.abdul.axi_api.repos;

import me.abdul.axi_api.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findAllByTeamIdAndReceiverIdAndSenderIdIsNot(int teamId, int receiverId, int senderId);
    List<Feedback> findAllByTeamIdAndReceiverIdAndSenderId(int teamId, int receiverId, int senderId);
    List<Feedback> findAllByTeamIdAndReceiverId(int teamId, int receiverId);
    List<Feedback> findAllByTeamId(int teamId);

    @Query("SELECT f FROM Feedback f JOIN f.form fo WHERE f.date BETWEEN :startDate AND :endDate AND f.team.id = :teamId AND f.receiver.id = :receiverId AND fo.category.id = :categoryId")
    List<Feedback> findAllByDateRangeAndTeamIdAndReceiverIdAndCategoryId(Instant startDate, Instant endDate, int teamId, int receiverId, int categoryId);

}
