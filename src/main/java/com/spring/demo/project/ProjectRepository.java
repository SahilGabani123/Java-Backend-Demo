package com.spring.demo.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByEmployeeId(Long employeeId);

    long countByEmployeeId(Long employeeId);
}