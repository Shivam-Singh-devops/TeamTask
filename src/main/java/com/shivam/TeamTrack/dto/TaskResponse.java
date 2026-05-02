package com.shivam.TeamTrack.dto;

import com.shivam.TeamTrack.model.Task;

import java.time.LocalDateTime;

public record TaskResponse(
        int id,
        String title,
        String description,
        int projectId,
        int assignedToUserId,
        String assignedToUserName,
        Task.TaskStatus status,
        LocalDateTime dueDate,
        LocalDateTime createdDate,
        boolean isOverdue
) {}