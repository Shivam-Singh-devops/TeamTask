package com.shivam.TeamTrack.auth.dto;

public record AuthResponse(String token, String email, String role) {
}
