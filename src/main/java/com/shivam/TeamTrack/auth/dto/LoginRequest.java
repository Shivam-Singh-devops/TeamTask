package com.shivam.TeamTrack.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(
        @JsonProperty("email")
        @JsonAlias({"username", "Email"})
        String email,
        String password
) {
}
