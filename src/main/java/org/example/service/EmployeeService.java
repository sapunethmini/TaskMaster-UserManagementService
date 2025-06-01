package org.example.service;

import org.example.dto.Employee;

import java.util.List;
public interface EmployeeService {

    /**
     * Add a new employee to the system
     * @param employee Employee object containing employee details
     * @throws IllegalArgumentException if employee data is invalid or email already exists
     */
    void addEmployee(Employee employee);

    /**
     * Retrieve all employees from the system
     * @return List of all employees
     */
    List<Employee> getAll();

    /**
     * Delete an employee by their ID
     * @param id Employee ID to delete
     * @throws IllegalArgumentException if employee with given ID doesn't exist
     */
    void deleteEmployeeById(Long id);

    /**
     * Update an existing employee's information
     * @param emp Employee object with updated information (must include ID)
     * @throws IllegalArgumentException if employee doesn't exist or email conflicts
     */
    void updateEmployee(Employee emp);

    /**
     * Find an employee by their ID
     * @param id Employee ID to search for
     * @return Employee object if found, empty Employee object if not found
     * @throws IllegalArgumentException if ID is null
     */
    Employee findById(Long id);

    /**
     * Find an employee by their first name
     * @param firstname First name to search for
     * @return Employee object if found, empty Employee object if not found
     * @throws IllegalArgumentException if firstname is null or empty
     */
    Employee findByfirstname(String firstname);

    /**
     * Get all employees belonging to a specific department
     * @param departmentId Department ID to filter by
     * @return List of employees in the specified department
     * @throws IllegalArgumentException if departmentId is null or empty
     */
    List<Employee> getAllByDepartment(String departmentId);

    /**
     * Get the count of employees in a specific department
     * @param departmentId Department ID to count employees for
     * @return Number of employees in the department as integer
     */
    int getEmployeeCountByDepartment(String departmentId);

    /**
     * Get the count of members in a specific department
     * @param departmentId Department ID to count members for
     * @return Number of members in the department as long
     */
    long getMembersCount(String departmentId);
}