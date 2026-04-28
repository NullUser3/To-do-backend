package com.example.collaborative.to_do_list.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.collaborative.to_do_list.dto.task.TaskDto.RecurrenceRule;
import com.example.collaborative.to_do_list.model.TaskList;
import com.example.collaborative.to_do_list.model.Task;

public interface TaskRepo extends JpaRepository<Task,UUID>{
    long countByList(TaskList list);

    @Query("select max(l.position) from Task l where l.createdBy.id = :userId")
    Optional<Integer> findMaxPosition(@Param("userId") UUID userId);

    List<Task> findAllByListIdAndCreatedById(UUID listId, UUID userId);
    List<Task> findAllByCreatedById(UUID userId);

    @Modifying
    @Query("UPDATE Task pl SET pl.position = pl.position - 1 WHERE pl.createdBy.id = :userId AND pl.position > :positionId")
    void decrementPositionsGreaterThenPositionId(@Param("userId") UUID userId,@Param("positionId") int positionId);

    List<Task> findAllByListIsNullAndCreatedById(UUID userId);

    List<Task> findByRecurrenceRule(RecurrenceRule rule);

    List<Task> findAllByRecurrenceRuleIn(List<RecurrenceRule> rules);

     
}
