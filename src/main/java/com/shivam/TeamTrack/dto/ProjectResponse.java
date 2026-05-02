package com.shivam.TeamTrack.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponse(
        int id,
        String name,
        String description,
        LocalDateTime createdDate,
        String creatorEmail,
        boolean canEdit,
        List<ProjectMemberDto> members
) {
}
