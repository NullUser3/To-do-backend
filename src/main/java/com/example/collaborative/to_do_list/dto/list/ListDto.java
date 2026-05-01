package com.example.collaborative.to_do_list.dto.list;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.example.collaborative.to_do_list.model.TaskList;
import com.example.collaborative.to_do_list.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public class ListDto {

	public record ListRequest(UUID id, @NotBlank(message = "Name cannot be blank") String name,
			@Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
					message = "Color must be a valid HEX format (#RGB or #RRGGBB)") String color,
			int position, User createdBy) {
	}

	public record ListId(UUID listId) {
	}

	public record UpdateListRequest(String name, @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
			message = "Color must be a valid HEX format (#RGB or #RRGGBB)") String color) {

	}

	public record ListResponse(UUID id, String name, String color, int position, OffsetDateTime createdAt,
			OffsetDateTime UpdatedAt) {
		public static ListResponse from(TaskList list) {
			return new ListResponse(list.getId(), list.getName(), list.getColor(), list.getPosition(),
					list.getCreatedAt(), list.getUpdatedAt());
		}
	}

	public record ListResponseAndCount(UUID id, String name, String color, int position, long taskCount,
			OffsetDateTime createdAt, OffsetDateTime UpdatedAt) {
		public static ListResponseAndCount from(TaskList list, long taskCount) {
			return new ListResponseAndCount(list.getId(), list.getName(), list.getColor(), list.getPosition(),
					taskCount, list.getCreatedAt(), list.getUpdatedAt());
		}
	}

	public record UserSummary(UUID id, String username) {
	}

}
