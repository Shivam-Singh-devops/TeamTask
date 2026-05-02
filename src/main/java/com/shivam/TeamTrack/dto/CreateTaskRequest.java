package com.shivam.TeamTrack.dto;


import java.time.LocalDateTime;

// CreateTaskRequest.java
public record CreateTaskRequest(
        String title,
        String description,
        int assignedToUserId,
        LocalDateTime dueDate
) {}

