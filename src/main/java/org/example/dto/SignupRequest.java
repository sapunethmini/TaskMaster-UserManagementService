package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request model for user registration")
public class SignupRequest {
    @Schema(description = "Desired username for the new account.", example = "newuser", required = true)
    private String username;

    @Schema(description = "Password for the new account.", example = "password123", required = true)
    private String password;
}