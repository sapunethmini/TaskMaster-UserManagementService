package org.example.controller;

import org.example.dto.LoginRequest;
import org.example.dto.SignupRequest;
import org.example.dto.TokenResponse;
import org.example.entity.User;
import org.example.entity.EmployeeEntity;
import org.example.model.NotificationEvent;
import org.example.service.AuthService;
import org.example.repository.UserRepository;
import org.example.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "APIs for user registration and login")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          EmployeeRepository employeeRepository,
                          PasswordEncoder passwordEncoder,
                          KafkaTemplate<String, Object> kafkaTemplate) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/signup")
    @Transactional
    @Operation(summary = "Register a new user", description = "Creates a new user account and an associated employee record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Username already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error during user creation")
    })
    public ResponseEntity<?> signUp(@RequestBody(description = "User registration details", required = true,
            content = @Content(schema = @Schema(implementation = SignupRequest.class)))
                                    @org.springframework.web.bind.annotation.RequestBody SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User(
                null,
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                "ROLE_EMPLOYEE"
        );

        try {
            User savedUser = userRepository.save(user);

            if ("ROLE_EMPLOYEE".equals(savedUser.getRole())) {
                createEmployeeRecord(savedUser);
            }

            try {
                NotificationEvent event = new NotificationEvent(
                        "USER_REGISTERED",
                        savedUser.getId(),
                        savedUser.getUsername(),
                        "Welcome to TaskApp",
                        "Welcome to TaskApp! Your account has been successfully created."
                );

                kafkaTemplate.send("user-events", event);
            } catch (Exception e) {
                System.err.println("Failed to send welcome notification: " + e.getMessage());
            }

            return ResponseEntity.status(201).body(
                    "User registered successfully. Employee record created with ID: " + savedUser.getId()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving user: " + e.getMessage());
        }
    }

    private void createEmployeeRecord(User user) {
        try {
            if (employeeRepository.findById(user.getId()).isPresent()) {
                System.out.println("Employee record already exists for user: " + user.getUsername());
                return;
            }

            EmployeeEntity employee = new EmployeeEntity();
            employee.setId(user.getId());
            employee.setFirstname(user.getUsername());
            employee.setLastname("");
            employee.setEmail(user.getUsername() + "@company.com");
            employee.setDepartmentId("DEFAULT");
            employee.setRoleId("21");
            employeeRepository.save(employee);
            System.out.println("Employee record created for user: " + user.getUsername() + " with ID: " + user.getId());

        } catch (Exception e) {
            System.err.println("Failed to create employee record: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create employee record", e);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user", description = "Logs in a user and returns a JWT token along with user details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public ResponseEntity<?> login(@RequestBody(description = "User login credentials", required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class)))
                                   @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        try {
            String token = authService.authenticateAndGenerateToken(request.getUsername(), request.getPassword());
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            TokenResponse response = new TokenResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid credentials: " + e.getMessage());
        }
    }
}