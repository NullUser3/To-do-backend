package com.example.collaborative.to_do_list.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.example.collaborative.to_do_list.adapter.UserAdapter;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListId;
import com.example.collaborative.to_do_list.dto.task.TaskDto.TaskRequest;
import com.example.collaborative.to_do_list.dto.task.TaskDto.TaskResponse;
import com.example.collaborative.to_do_list.dto.task.TaskDto.RecurrenceRule;
import com.example.collaborative.to_do_list.dto.task.TaskDto.UpdateTaskRequest;
import com.example.collaborative.to_do_list.exception.ResourceNotFoundException;
import com.example.collaborative.to_do_list.exception.UserNotFoundException;
import com.example.collaborative.to_do_list.model.TaskList;
import com.example.collaborative.to_do_list.model.Task;
import com.example.collaborative.to_do_list.model.User;
import com.example.collaborative.to_do_list.repo.ListRepo;
import com.example.collaborative.to_do_list.repo.TaskRepo;
import com.example.collaborative.to_do_list.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepo taskRepo;

	private final UserRepo userRepo;

	private final ListRepo listRepo;

	@Transactional
	public TaskResponse createTask(TaskRequest TaskRequest, Authentication authentication) {

		UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();

		User creator = userRepo.findById(userDetails.getId())
			.orElseThrow(() -> new UserNotFoundException(userDetails.getId()));

		int newPosition = taskRepo.findMaxPosition(creator.getId()).orElse(0) + 1;

		TaskList list = null;

		if (TaskRequest.listId() != null) {
			list = listRepo.findById(TaskRequest.listId())
				.orElseThrow(() -> new ResourceNotFoundException(TaskRequest.listId()));
		}

		Task task = Task.builder()
			.title(TaskRequest.title())
			.description(TaskRequest.description())
			.dueDate(TaskRequest.dueDate())
			.reminderAt(TaskRequest.reminderAt())
			.list(list)
			.createdBy(creator)
			.recurrenceRule(TaskRequest.recurrenceRule())
			.nextRecurrence(TaskRequest.nextRecurrence())
			.position(newPosition)
			.build();

		return TaskResponse.from(taskRepo.save(task));
	}

	@Transactional
	public List<TaskResponse> getTasksByListId(ListId request, Authentication authentication) {
		UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();

		List<Task> tasks = taskRepo.findAllByListIdAndCreatedById(request.listId(), userDetails.getId());

		List<TaskResponse> responses = new ArrayList<>();
		for (Task task : tasks) {
			responses.add(TaskResponse.from(task));
		}
		return responses;
	}

	@Transactional
	public void deleteTaskById(UUID id, Authentication authentication) {
		UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();
		Task task = taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));

		if (!task.getCreatedBy().getId().equals(userDetails.getId())) {
			throw new AccessDeniedException("You don't own this list.");
		}

		taskRepo.decrementPositionsGreaterThenPositionId(task.getCreatedBy().getId(), task.getPosition());

		taskRepo.delete(task);
	}

	@Transactional
	public TaskResponse updateTask(UUID id, UpdateTaskRequest request, Authentication authentication) {
		UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();
		Task task = taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		if (!task.getCreatedBy().getId().equals(userDetails.getId())) {
			throw new AccessDeniedException("You don't own this list.");
		}
		if (request.listId() != null) {
			TaskList list = listRepo.findById(request.listId())
				.orElseThrow(() -> new ResourceNotFoundException(request.listId()));
			task.setList(list);
			System.out.println("the given list is :" + list);
		}
		else {
			// Explicitly keep the existing list if not provided
			// task.setList(task.getList()); // This line is technically not needed as
			// it's already set
		}
		if (request.title() != null) {
			task.setTitle(request.title());
		}
		if (request.description() != null) {
			task.setDescription(request.description());
		}
		if (request.dueDate() != null) {
			task.setDueDate(request.dueDate());
		}
		if (request.recurrenceRule() != null) {
			task.setRecurrenceRule(request.recurrenceRule());
		}
		if (request.reminderAt() != null) {
			task.setReminderAt(request.reminderAt());
		}
		if (request.isCompleted() != null) {
			task.setCompleted(request.isCompleted());
		}

		return TaskResponse.from(task);

	}

	@Transactional
	public List<TaskResponse> getAllTasks(Authentication authentication) {
		UserAdapter userDetails = (UserAdapter) authentication.getPrincipal();
		List<Task> tasks = taskRepo.findAllByCreatedById(userDetails.getId());
		return tasks.stream().map(task -> TaskResponse.from(task)).collect(Collectors.toList());
	}

	@Scheduled(cron = "0 0 * * * *") // every hour; adjust as needed
	public void resetRecurringTasks() {
		List<Task> tasks = taskRepo
			.findAllByRecurrenceRuleIn(List.of(RecurrenceRule.DAILY, RecurrenceRule.WEEKLY, RecurrenceRule.MONTHLY));

		OffsetDateTime now = OffsetDateTime.now();
		System.out.println("Now: " + now);

		for (Task task : tasks) {
			if (task.isCompleted() && task.getDueDate().isBefore(now)) {
				System.out.println("Resetting task: " + task.getTitle() + " (" + task.getRecurrenceRule() + ")");

				task.setCompleted(false);

				// Advance dueDate based on recurrence rule
				switch (task.getRecurrenceRule()) {
					case DAILY -> task.setDueDate(task.getDueDate().plusDays(1));
					case WEEKLY -> task.setDueDate(task.getDueDate().plusWeeks(1));
					case MONTHLY -> task.setDueDate(task.getDueDate().plusMonths(1));
				}

				taskRepo.save(task);
			}
		}
	}

}
