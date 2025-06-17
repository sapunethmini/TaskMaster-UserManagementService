package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response model containing the authentication token and user details after a successful login.")
public class AuthResponse {

    @Schema(description = "JWT (JSON Web Token) provided upon successful authentication.",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNjc5ODUyMjAyLCJleHAiOjE2Nzk4ODgyMDJ9.fake_token_signature")
    private String token;

    @Schema(description = "Username of the authenticated user.",
            example = "johndoe")
    private String username;

    @Schema(description = "Role assigned to the authenticated user, which determines their access level.",
            example = "ROLE_EMPLOYEE")
    private String role;

    @Schema(description = "The unique identifier of the authenticated user.",
            example = "101")
    private Long userId;
}