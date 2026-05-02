package com.shivam.TeamTrack.auth.dto;

import com.shivam.TeamTrack.model.User;

public record RegisterRequest(String name, String email, String password, User.Role role) {
}
