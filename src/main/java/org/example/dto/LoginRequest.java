package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request model for user login")
public class LoginRequest {
    @Schema(description = "The user's username.", example = "testuser", required = true)
    private String username;

    @Schema(description = "The user's password.", example = "password123", required = true)
    private String password;
}