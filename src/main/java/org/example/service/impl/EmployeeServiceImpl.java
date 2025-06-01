package org.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.Employee;
import org.example.entity.EmployeeEntity;
import org.example.repository.EmployeeRepository;
import org.example.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository repository;

    @Override
    public void addEmployee(Employee employee) {
        logger.info("Adding employee: {}", employee.getFirstname());

        // Check for duplicate email
        if (repository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Employee with email " + employee.getEmail() + " already exists");
        }

        EmployeeEntity entity = convertToEntity(employee);
        repository.save(entity);
        logger.info("Successfully added employee with ID: {}", entity.getId());
    }

    @Override
    public List<Employee> getAll() {
        logger.info("Fetching all employees");
        List<EmployeeEntity> entities = repository.findAll();
        logger.info("Found {} employees", entities.size());

        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional  // Add this annotation
    public void deleteEmployeeById(Long id) {
        logger.info("Attempting to delete employee with ID: {}", id);

        // Validate input
        if (id == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }

        // Check if employee exists
        if (!repository.existsById(id)) {
            logger.warn("Employee with ID {} not found for deletion", id);
            throw new IllegalArgumentException("Employee with ID " + id + " not found");
        }

        try {
            repository.deleteById(id);
            logger.info("Successfully deleted employee with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting employee with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete employee: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateEmployee(Employee emp) {
        logger.info("Updating employee with ID: {}", emp.getId());

        EmployeeEntity existing = repository.findById(emp.getId())
                .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + emp.getId() + " not found"));

        // Check email uniqueness if email is being changed
        if (!existing.getEmail().equals(emp.getEmail()) && repository.existsByEmail(emp.getEmail())) {
            throw new IllegalArgumentException("Employee with email " + emp.getEmail() + " already exists");
        }

        // Update fields
        existing.setFirstname(emp.getFirstname());
        existing.setLastname(emp.getLastname());
        existing.setEmail(emp.getEmail());
        existing.setDepartmentId(emp.getDepartmentId());
        existing.setRoleId(emp.getRoleId());

        repository.save(existing);
        logger.info("Successfully updated employee with ID: {}", emp.getId());
    }

    @Override
    public Employee findById(Long id) {
        logger.info("Finding employee by ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }

        EmployeeEntity entity = repository.findById(id)
                .orElse(null);

        if (entity == null) {
            logger.warn("Employee with ID {} not found", id);
            return new Employee(); // Maintaining your existing behavior
        }

        return convertToDTO(entity);
    }

    @Override
    public Employee findByfirstname(String firstname) {
        logger.info("Finding employee by firstname: {}", firstname);

        if (firstname == null || firstname.trim().isEmpty()) {
            throw new IllegalArgumentException("Firstname cannot be null or empty");
        }

        EmployeeEntity entity = repository.findByFirstname(firstname.trim())
                .orElse(null);

        if (entity == null) {
            logger.warn("Employee with firstname {} not found", firstname);
            return new Employee(); // Maintaining your existing behavior
        }

        return convertToDTO(entity);
    }

    @Override
    public List<Employee> getAllByDepartment(String departmentId) {
        logger.info("Searching for employees with department ID: {}", departmentId);

        if (departmentId == null || departmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Department ID cannot be null or empty");
        }

        List<EmployeeEntity> entities = repository.findByDepartmentId(departmentId.trim());
        logger.info("Found {} employees in department {}", entities.size(), departmentId);

        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public int getEmployeeCountByDepartment(String departmentId) {
        logger.info("Counting employees in department: {}", departmentId);

        if (departmentId == null || departmentId.trim().isEmpty()) {
            return 0;
        }

        long count = repository.countByDepartmentId(departmentId.trim());
        logger.info("Found {} employees in department {}", count, departmentId);
        return (int) count;
    }

    @Override
    public long getMembersCount(String departmentId) {
        logger.info("Getting member count for department: {}", departmentId);

        if (departmentId == null || departmentId.trim().isEmpty()) {
            return 0;
        }

        return repository.countByDepartmentId(departmentId.trim());
    }

    // Helper methods for conversion
    private EmployeeEntity convertToEntity(Employee employee) {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(employee.getId());
        entity.setFirstname(employee.getFirstname());
        entity.setLastname(employee.getLastname());
        entity.setEmail(employee.getEmail());
        entity.setDepartmentId(employee.getDepartmentId());
        entity.setRoleId(employee.getRoleId());
        return entity;
    }

    private Employee convertToDTO(EmployeeEntity entity) {
        Employee employee = new Employee();
        employee.setId(entity.getId());
        employee.setFirstname(entity.getFirstname());
        employee.setLastname(entity.getLastname());
        employee.setEmail(entity.getEmail());
        employee.setDepartmentId(entity.getDepartmentId());
        employee.setRoleId(entity.getRoleId());
        return employee;
    }
}