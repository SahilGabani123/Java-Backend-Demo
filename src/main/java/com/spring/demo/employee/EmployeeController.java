package com.spring.demo.employee;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.demo.project.Project;
import com.spring.demo.project.ProjectRepository;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
class EmployeeController {

	private final EmployeeRepository repository;
    private final ProjectRepository projectRepository;

	EmployeeController(EmployeeRepository repository, ProjectRepository projectRepository) {
		this.repository = repository;
		this.projectRepository = projectRepository;
		
	}

	@GetMapping("/employees")
	public ResponseEntity<ApiResponse> all(
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "perPage", defaultValue = "10") int perPage,
			HttpServletRequest request
	) {

		PageRequest pageable = PageRequest.of(page - 1, perPage);
		Page<Employee> employeePage = repository.findAll(pageable);
	    employeePage.getContent().forEach(employee -> {
	    	  Long count = projectRepository.countByEmployeeId(employee.getId());
			  employee.setProjectCount(count);
			  employee.setProjects(null);
	    });

		String baseUrl = request.getRequestURI();

		String nextPageUrl = employeePage.hasNext() ? baseUrl + "?page=" + (page + 1) + "&perPage=" + perPage : null;

		String prevPageUrl = employeePage.hasPrevious() ? baseUrl + "?page=" + (page - 1) + "&perPage=" + perPage
				: null;

		PageMeta meta = new PageMeta(page, perPage, employeePage.getTotalElements(), employeePage.getTotalPages(),
				nextPageUrl, prevPageUrl);
		
		  return ResponseEntity.ok(
		            new ApiResponse<>(
		                    true,
		                    employeePage.isEmpty()
		                            ? "No employees found"
		                            : "Employee list fetched successfully",
		                    employeePage.getContent(),
		                    meta
		            )
		    );
	}
	
	// ================== CREATE ==================
	@PostMapping(
            value = "/employees",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<ApiResponse> createEmployee(
            @RequestParam("name") String name,
            @RequestParam("field") String field,
            @RequestParam("joining_date") String joiningDateStr,
            @RequestParam(value = "leaving_date", required = false) String leavingDateStr,
            @RequestParam("position") String position,
            @RequestParam("company_email") String companyEmail,
            @RequestParam("personal_email") String personalEmail,
            @RequestParam("phone_number") String phoneNumber
    ) {
        Instant joiningDate = toInstant(joiningDateStr);
        Instant leavingDate = toInstant(leavingDateStr);
        Employee employee = new Employee(
                name, position, joiningDate, leavingDate, 0,
                personalEmail, companyEmail, field, phoneNumber, new ArrayList<>(), 0L
        );
        repository.save(employee);
        return ResponseEntity.ok(
                new ApiResponse(true, "Employee created successfully", employee)
        );
    }

	@GetMapping("/employees/{id}")
	ApiResponse one(@PathVariable("id") Long id) {
		 Employee employee = repository.findById(id)
		            .orElseThrow(() -> new EmployeeNotFoundException(id));

		    List<Project> projects = projectRepository.findByEmployeeId(id);
		    employee.setProjects(projects);
		    employee.setProjectCount(null);


		    return new ApiResponse(true, "Employee details fetched successfully", employee);
	}

	@DeleteMapping("/employees/{id}")
	public ResponseEntity<ApiResponse> delete(@PathVariable("id") Long id) {

		if (!repository.existsById(id)) {
			throw new EmployeeNotFoundException(id);
		}

		repository.deleteById(id);

		return ResponseEntity.ok(new ApiResponse(true, "Employee deleted successfully"));
	}

	// ================== UPDATE (PARTIAL) ==================
	@PutMapping("/employees/{id}")
	public ResponseEntity<ApiResponse> updateEmployee(
	        @PathVariable("id") Long id,

	        @RequestParam(value = "name", required = false) String name,

	        @RequestParam(value = "position", required = false) String position,

	        @RequestParam(value = "joining_date", required = false) String joiningDateStr,

	        @RequestParam(value = "leaving_date", required = false) String leavingDateStr,

	        @RequestParam(value = "company_email", required = false)
	        @Email(message = "Invalid company email format")
	        String companyEmail,

	        @RequestParam(value = "personal_email", required = false)
	        @Email(message = "Invalid personal email format")
	        String personalEmail,

	        @RequestParam(value = "phone_number", required = false) String phoneNumber,

	        @RequestParam(value = "field", required = false) String field
	) {

		Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

		if (name != null && !name.isEmpty()) {
			employee.setName(name);
		}
		
		if (position != null && !position.isEmpty()) {
			employee.setPosition(position);
		}

		employee.setLeavingDate(leavingDateStr);
		
		if (joiningDateStr != null && !joiningDateStr.isEmpty()) {
			employee.setJoiningDate(leavingDateStr);
		}

		if (position != null && !position.isEmpty()) {
			employee.setPosition(position);
		}

		if (companyEmail != null && !companyEmail.isEmpty()) {
			employee.setCompanyEmail(companyEmail);
		}
		if (personalEmail != null && !personalEmail.isEmpty()) {
			employee.setPersonalEmail(personalEmail);
		}

		if (phoneNumber != null && !phoneNumber.isEmpty()) {
			employee.setPhoneNumber(phoneNumber);
		}

		if (field != null && !field.isEmpty()) {
			employee.setField(field);
		}

		repository.save(employee);

		return ResponseEntity.ok(new ApiResponse(true, "Employee updated successfully", employee));
	}
	
	public static Instant toInstant(String date) {
	    if (date == null || date.isBlank()) {
	        return null;
	    }

	    DateTimeFormatter formatter =
	            DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    return LocalDate.parse(date, formatter)
	            .atStartOfDay(ZoneId.systemDefault())
	            .toInstant();
	}
}
