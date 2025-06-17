package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response model containing the JWT and user details after a successful login.")
public class TokenResponse {
    @Schema(description = "JWT authentication token.", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "User's unique ID.", example = "1")
    private Long id;

    @Schema(description = "User's username.", example = "testuser")
    private String username;

    @Schema(description = "User's role.", example = "ROLE_EMPLOYEE")
    private String role;
}