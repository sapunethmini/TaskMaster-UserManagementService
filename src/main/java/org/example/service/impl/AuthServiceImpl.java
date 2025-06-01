package org.example.service.impl;
import org.example.entity.User;
import org.example.entity.EmployeeEntity;
import org.example.model.NotificationEvent;
import org.example.repository.UserRepository;
import org.example.repository.EmployeeRepository;
import org.example.service.AuthService;
import org.example.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthServiceImpl(UserRepository userRepository,
                           EmployeeRepository employeeRepository,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder,
                           KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String authenticateAndGenerateToken(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Temporarily comment out employee creation to fix login issue
        // If user has ROLE_EMPLOYEE role, create employee record if it doesn't exist
        // if ("ROLE_EMPLOYEE".equals(user.getRole())) {
        //     createEmployeeRecordIfNotExists(user);
        // }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("userId", user.getId());

        // Send login notification
        sendLoginNotification(user);

        return jwtUtil.generateToken(username, user.getId(), claims);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    // Temporarily commented out - employee creation logic
    // Uncomment and fix when you need to create employee records during login
    /*
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createEmployeeRecordIfNotExists(User user) {
        try {
            // Check if employee record already exists by searching for user reference
            // You might need to adjust this based on your EmployeeEntity structure
            Optional<EmployeeEntity> existingEmployee = findEmployeeByUserId(user.getId());

            if (existingEmployee.isEmpty()) {
                EmployeeEntity employee = new EmployeeEntity();

                // Don't set the ID - let it be auto-generated
                // If you need to link to user, add a userId field to EmployeeEntity
                employee.setFirstname(user.getUsername());
                employee.setLastname(""); // Set appropriate default or get from user
                employee.setEmail(""); // Set appropriate default or get from user
                employee.setDepartmentId("DEFAULT"); // Set appropriate default
                employee.setRoleId("EMPLOYEE"); // Set appropriate default

                // If your EmployeeEntity has a userId field to link back to User:
                // employee.setUserId(user.getId());

                employeeRepository.save(employee);
            }
        } catch (Exception e) {
            // Log error but don't fail login
            System.err.println("Failed to create employee record: " + e.getMessage());
            e.printStackTrace();
            // Don't rethrow the exception to avoid affecting the login process
        }
    }

    // Helper method to find employee by user ID
    // You might need to adjust this based on your EmployeeEntity structure
    private Optional<EmployeeEntity> findEmployeeByUserId(Long userId) {
        // If your EmployeeEntity has a userId field:
        // return employeeRepository.findByUserId(userId);

        // If you're using the same ID for both User and Employee:
        // return employeeRepository.findById(userId);

        // For now, assuming no existing employee record
        return Optional.empty();
    }
    */

    private void sendLoginNotification(User user) {
        try {
            NotificationEvent event = new NotificationEvent(
                    "USER_LOGGED_IN",
                    user.getId(),
                    user.getUsername(), // Using username as email since email field is not in User entity
                    "Login Successful",
                    "You have successfully logged into your account."
            );

            kafkaTemplate.send("user-events", event);
        } catch (Exception e) {
            // Log error but don't fail login
            System.err.println("Failed to send login notification: " + e.getMessage());
        }
    }
}