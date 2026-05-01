package com.example.collaborative.to_do_list.dto.user;

import java.util.UUID;
import com.example.collaborative.to_do_list.model.User;

public class UserResponse {
	
	public record UserResponsePublic(UUID id, String username, String email, String ImageUrl) {
	public static UserResponsePublic in(User user) {
		return new UserResponsePublic(user.getId(), user.getUsername(), user.getEmail(), user.getProfileImageUrl());
	}

}


public record UserResponsePrivate(String username, String email, String ImageUrl) {
	public static UserResponsePrivate in(User user) {
		return new UserResponsePrivate(user.getUsername(), user.getEmail(), user.getProfileImageUrl());
	}

}
}