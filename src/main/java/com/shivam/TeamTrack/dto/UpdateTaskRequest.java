package com.shivam.TeamTrack.dto;

import com.shivam.TeamTrack.model.Task;

import java.time.LocalDateTime;

public record UpdateTaskRequest(
        String title,
        String description,
        Task.TaskStatus status,
        int assignedToUserId,
        LocalDateTime dueDate
) {}
