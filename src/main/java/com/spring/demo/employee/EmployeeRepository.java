package com.spring.demo.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	 @Query("""
		        SELECT e FROM Employee e
		        WHERE (:field IS NULL OR LOWER(e.field) = LOWER(:field))
		        AND (:position IS NULL OR LOWER(e.position) = LOWER(:position))
		          AND (
		                :search IS NULL OR
		                LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
		                LOWER(e.companyEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR
		                LOWER(e.personalEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR
		                e.phoneNumber LIKE CONCAT('%', :search, '%')
		          )
		    """)
	 Page<Employee> searchEmployees(
			 @Param("field") String field,
	            @Param("position") String position,
	            @Param("search") String search,
	            Pageable pageable);

}
