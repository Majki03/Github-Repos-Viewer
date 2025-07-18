package com.example.github_repos_viewer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Opcjonalnie, możemy użyć @ResponseStatus, ale GlobalExceptionHandler daje nam większą kontrolę
// @ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}