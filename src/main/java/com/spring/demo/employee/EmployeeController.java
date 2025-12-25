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

//	@PutMapping("/employees/{id}")
//	Employee replaceEmployee(
//	        @RequestBody Employee newEmployee,
//	        @PathVariable("id") Long id) {
//
//	    Employee employee = repository.findById(id)
//	            .orElseThrow(() -> new EmployeeNotFoundException(id));
//
//	    if (newEmployee.getName() != null) {
//	        employee.setName(newEmployee.getName());
//	    }
//
//	    if (newEmployee.getRole() != null) {
//	        employee.setRole(newEmployee.getRole());
//	    }
//
//	    return repository.save(employee);
//	}

	// ================== UPDATE (PARTIAL) ==================
	@PutMapping("/employees/{id}")
	public ResponseEntity<ApiResponse> updateEmployee(@PathVariable("id") Long id, @RequestBody Employee newEmployee) {

		Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

		if (newEmployee.getName() != null)
			employee.setName(newEmployee.getName());

		if (newEmployee.getPosition() != null)
			employee.setPosition(newEmployee.getPosition());

		if (newEmployee.getJoiningDate() != null)
			employee.setJoiningDate(newEmployee.getJoiningDate());
		
			employee.setLeavingDate(newEmployee.getLeavingDate());

		if (newEmployee.getPersonalEmail() != null)
			employee.setPersonalEmail(newEmployee.getPersonalEmail());

		if (newEmployee.getCompanyEmail() != null)
			employee.setCompanyEmail(newEmployee.getCompanyEmail());

		if (newEmployee.getField() != null)
			employee.setField(newEmployee.getField());

		if (newEmployee.getPhoneNumber() != null)
			employee.setPhoneNumber(newEmployee.getPhoneNumber());

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