package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents an employee data transfer object.")
public class Employee {
    @Schema(description = "Unique identifier of the employee.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "First name of the employee.", example = "John")
    private String firstname;

    @Schema(description = "Last name of the employee.", example = "Doe")
    private String lastname;

    @Schema(description = "Email address of the employee.", example = "john.doe@company.com")
    private String email;

    @Schema(description = "ID of the department the employee belongs to.", example = "DEPT-01")
    private String departmentId;

    @Schema(description = "ID of the role assigned to the employee.", example = "21")
    private String roleId;
}