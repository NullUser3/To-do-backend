package com.example.collaborative.to_do_list.dto.task;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.example.collaborative.to_do_list.model.TaskList;
import com.example.collaborative.to_do_list.model.Task;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskDto {

	public record TaskRequest(

			@NotBlank(message = "Title is required") @Size(max = 255,
					message = "Title must be at most 255 characters") String title,

			@Size(max = 1000, message = "Description must be at most 1000 characters") String description,

			@FutureOrPresent(message = "Due date must be in the present or future") OffsetDateTime dueDate,

			@Future(message = "reminder must be in the future") OffsetDateTime reminderAt,

			UUID listId,

			RecurrenceRule recurrenceRule, OffsetDateTime nextRecurrence) {
	}

	public record UpdateTaskRequest(@Size(max = 255, message = "Title must be at most 255 characters") String title,

			@Size(max = 1000, message = "Description must be at most 1000 characters") String description,

			@FutureOrPresent(message = "Due date must be in the present or future") OffsetDateTime dueDate,

			@Future(message = "reminder must be in the future") OffsetDateTime reminderAt,

			UUID listId,

			RecurrenceRule recurrenceRule, OffsetDateTime nextRecurrence, Boolean isCompleted) {
	}

	public record TaskResponse(UUID id, String title, String description, OffsetDateTime dueDate,
			OffsetDateTime reminderAt, UUID listId, String listName, RecurrenceRule recurrenceRule,
			OffsetDateTime nextRecurrence, int position, OffsetDateTime createdAt, boolean isCompleted) {
		public static TaskResponse from(Task Task) {
			TaskList list = Task.getList();
			return new TaskResponse(Task.getId(), Task.getTitle(), Task.getDescription(), Task.getDueDate(),
					Task.getReminderAt(), list != null ? list.getId() : null, list != null ? list.getName() : null,
					Task.getRecurrenceRule(), Task.getNextRecurrence(), Task.getPosition(), Task.getCreatedAt(),
					Task.isCompleted());
		}
	}

	public enum RecurrenceRule {

		DAILY, WEEKLY, MONTHLY

	}

}
