package org.example.controller;

import org.example.dto.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/emp-controller")
@Tag(name = "Employee Controller", description = "APIs for managing employee data")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService service;

    @PostMapping("/add-employee")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new employee", description = "Creates a new employee record in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid employee data provided")
    })
    public void addEmployeeDetails(@RequestBody Employee employee) {
        logger.info("Adding new employee: {}", employee.getFirstname());
        service.addEmployee(employee);
    }

    @GetMapping("/get-all")
    @Operation(summary = "Get all employees", description = "Retrieves a list of all employee records.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of employees")
    public List<Employee> getAll() {
        logger.info("Fetching all employees");
        return service.getAll();
    }

    @DeleteMapping("/delete-emp/{id}")
    @Operation(summary = "Delete an employee", description = "Deletes an employee record by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteEmployee(@Parameter(description = "ID of the employee to be deleted", required = true) @PathVariable Long id) {
        try {
            logger.info("Attempting to delete employee with ID: {}", id);
            service.deleteEmployeeById(id);
            logger.info("Successfully deleted employee with ID: {}", id);
            return ResponseEntity.accepted().body("successfully deleted");
        } catch (IllegalArgumentException e) {
            logger.error("Employee not found for deletion: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting employee with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting employee: " + e.getMessage());
        }
    }

    @PutMapping("/update-emp/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update an employee", description = "Updates the details of an existing employee by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public void updateEmployee(@Parameter(description = "ID of the employee to be updated", required = true) @PathVariable Long id, @RequestBody Employee emp) {
        logger.info("Updating employee with ID: {}", id);
        emp.setId(id);
        service.updateEmployee(emp);
    }

    @GetMapping("/find-by-id/{id}")
    @Operation(summary = "Find employee by ID", description = "Retrieves a single employee's details by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the employee"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<Employee> findById(@Parameter(description = "ID to search for an employee", required = true) @PathVariable Long id) {
        logger.info("Finding employee by ID: {}", id);
        Employee employee = service.findById(id);
        return ResponseEntity.ok(employee);
    }

    // ... other endpoints with similar annotations ...

    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Checks the operational status of the employee service.")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public String checkHealth() {
        return "User service routing is working!";
    }
}