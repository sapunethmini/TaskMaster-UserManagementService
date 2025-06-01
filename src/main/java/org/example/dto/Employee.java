package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private Long id;

    private String firstname;

    private String lastname;

    private String email;

    @JsonProperty("department_Id")
    private String departmentId;

    @JsonProperty("role_Id")
    private String roleId;


}
