package com.shivam.TeamTrack.dto;

import com.shivam.TeamTrack.model.ProjectRole;

public record ProjectMemberDto(String email, String name, ProjectRole role) {
}
