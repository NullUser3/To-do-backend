package com.example.collaborative.to_do_list.exception;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ApiError {

	private int status;

	private String error;

	private String message;

	private LocalDateTime timestamp;

	public ApiError(int status, String error, String message, LocalDateTime timestamp) {
		this.status = status;
		this.error = error;
		this.message = message;
		this.timestamp = timestamp;
	}

}
