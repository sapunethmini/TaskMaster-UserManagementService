package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee", indexes = {
        @Index(name = "idx_department_id", columnList = "department_id"),
        @Index(name = "idx_firstname", columnList = "firstname"),
        @Index(name = "idx_email", columnList = "email")
})
public class EmployeeEntity {

    @Id
    private Long id;

    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "department_id", nullable = false)
    private String departmentId;

    @Column(name = "role_id", nullable = false)
    private String roleId;
}