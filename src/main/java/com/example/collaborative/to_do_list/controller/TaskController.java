package com.example.collaborative.to_do_list.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.collaborative.to_do_list.dto.list.ListDto.ListId;
import com.example.collaborative.to_do_list.dto.task.TaskDto.TaskRequest;
import com.example.collaborative.to_do_list.dto.task.TaskDto.TaskResponse;
import com.example.collaborative.to_do_list.dto.task.TaskDto.UpdateTaskRequest;
import com.example.collaborative.to_do_list.service.TaskService;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/task")
@AllArgsConstructor
public class TaskController {

	private final TaskService taskService;

	@PostMapping("/createTask")
	public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest TaskRequest,
			Authentication authentication) {

		TaskResponse task = taskService.createTask(TaskRequest, authentication);

		return ResponseEntity.ok(task);
	}

	@PostMapping("/getTasksFromList")
	public List<TaskResponse> getTasksFromList(@RequestBody ListId ListId, Authentication authentication) {
		List<TaskResponse> responses = taskService.getTasksByListId(ListId, authentication);
		return responses;
	}

	@PutMapping("/updateTask/{id}")
	public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID id, @RequestBody UpdateTaskRequest request,
			Authentication authentication) {
		TaskResponse task = taskService.updateTask(id, request, authentication);
		return ResponseEntity.ok(task);
	}

	@DeleteMapping("/deleteTask/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable UUID id, Authentication authentication) {
		taskService.deleteTaskById(id, authentication);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/getAllTasks")
	public ResponseEntity<List<TaskResponse>> getMethodName(Authentication authentication) {
		List<TaskResponse> tasks = taskService.getAllTasks(authentication);
		return ResponseEntity.ok(tasks);
	}

}
