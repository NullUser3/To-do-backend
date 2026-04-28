package com.example.collaborative.to_do_list.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.example.collaborative.to_do_list.dto.task.TaskDto.RecurrenceRule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.collaborative.to_do_list.model.TaskList;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "personal_tasks")
@ToString(exclude = {"list", "createdBy", "assignedTo"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    @Builder.Default
    private OffsetDateTime dueDate=null;

     @Column(name = "reminder_at")
     @Builder.Default
    private OffsetDateTime reminderAt=null;

    @Column(name = "is_completed")
    @Builder.Default
    private boolean isCompleted = false;

    @Column(name = "completed_at")
    @Builder.Default
    private OffsetDateTime completedAt=null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    @JsonIgnore
    @Builder.Default
    private TaskList list=null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    @Builder.Default
    private User assignedTo=null;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_rule", length = 100)
    @Builder.Default
    private RecurrenceRule recurrenceRule=null;

    @Column(name = "next_recurrence")
    @Builder.Default
    private OffsetDateTime nextRecurrence=null;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

     @Column(nullable = false)
    private int position;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public void setList(TaskList list) {
    if (this.list != null) {
        this.list.getTasks().remove(this);
    }
    this.list = list;
    if (list != null) {
        list.getTasks().add(this);
    }
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Task)) return false;
    Task task = (Task) o;
    return id != null && id.equals(task.id);
}

@Override
public int hashCode() {
    return getClass().hashCode();
}
}
