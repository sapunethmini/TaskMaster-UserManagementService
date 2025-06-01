package org.example.repository;

import org.example.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Optional<EmployeeEntity> findByFirstname(String firstname);

    @Query("SELECT COUNT(e) FROM EmployeeEntity e WHERE e.departmentId = :departmentId")
    long countByDepartmentId(@Param("departmentId") String departmentId);

    @Query("SELECT e FROM EmployeeEntity e WHERE e.departmentId = :departmentId")
    List<EmployeeEntity> findByDepartmentId(@Param("departmentId") String departmentId);

    boolean existsByEmail(String email);
}