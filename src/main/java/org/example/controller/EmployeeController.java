package org.example.controller;

import org.example.dto.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/emp-controller")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService service;

    @PostMapping("/add-employee")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEmployeeDetails( @RequestBody Employee employee) {
        logger.info("Adding new employee: {}", employee.getFirstname());
        service.addEmployee(employee);
    }

    @GetMapping("/get-all")
    public List<Employee> getAll() {
        logger.info("Fetching all employees");
        return service.getAll();
    }

    @DeleteMapping("/delete-emp/{id}")  // Added missing "/" at the beginning
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
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
    public void updateEmployee(@PathVariable Long id, @RequestBody Employee emp) {
        logger.info("Updating employee with ID: {}", id);
        emp.setId(id);
        service.updateEmployee(emp);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<Employee> findById(@PathVariable Long id) {
        logger.info("Finding employee by ID: {}", id);
        Employee employee = service.findById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/find-by-name/{firstname}")
    public Employee findByfirstname(@PathVariable String firstname) {
        logger.info("Finding employee by firstname: {}", firstname);
        return service.findByfirstname(firstname);
    }

    @GetMapping("/getAllByDepartment/{department_Id}")
    public List<Employee> getAllByDepartment(@PathVariable String department_Id) {
        logger.info("Fetching employees for department: {}", department_Id);
        return service.getAllByDepartment(department_Id);
    }

    @GetMapping("/count-by-department/{department_Id}")
    public ResponseEntity<Integer> getEmployeeCountByDepartment(@PathVariable String department_Id) {
        logger.info("Counting employees in department: {}", department_Id);
        int count = service.getEmployeeCountByDepartment(department_Id);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count")
    public long getMembersCount(@RequestParam String departmentId) {
        logger.info("Getting member count for department: {}", departmentId);
        return service.getMembersCount(departmentId);
    }


    @GetMapping("/health")
    public String checkHealth() {
        return "User service routing is working!";
    }
}