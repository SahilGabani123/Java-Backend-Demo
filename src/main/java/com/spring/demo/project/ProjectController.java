package com.spring.demo.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.demo.employee.ApiResponse;
import com.spring.demo.employee.Employee;
import com.spring.demo.employee.EmployeeNotFoundException;
import com.spring.demo.employee.EmployeeRepository;
import com.spring.demo.employee.PageMeta;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/v1")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectController(ProjectRepository projectRepository, EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    // -------------------- GET all projects --------------------
    @GetMapping("projects")
    public ResponseEntity<ApiResponse> getAllProjects(
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "perPage", defaultValue = "10") int perPage,
			HttpServletRequest request
    ) {
    	PageRequest pageable = PageRequest.of(page - 1, perPage);
    	Page<Project> projectPage = projectRepository.findAll(pageable);
 		String baseUrl = request.getRequestURI();
 		String nextPageUrl = projectPage.hasNext() ? baseUrl + "?page=" + (page + 1) + "&perPage=" + perPage : null;
 		String prevPageUrl = projectPage.hasPrevious() ? baseUrl + "?page=" + (page - 1) + "&perPage=" + perPage
 				: null;
 		PageMeta meta = new PageMeta(page, perPage, projectPage.getTotalElements(), projectPage.getTotalPages(),
 				nextPageUrl, prevPageUrl);
         return ResponseEntity.ok(
                 new ApiResponse<>(
                         true,
                         projectPage.getContent().isEmpty()
                                 ? "No projects found"
                                 : "Projects list fetched successfully",
                         projectPage.getContent(),
                         meta
                 )
         );
    }

    // -------------------- GET project by ID --------------------
    @GetMapping("projects/{id}")
    public ResponseEntity<ApiResponse> getProjectById(@PathVariable("id") Long id) {
        
    	  Project project = projectRepository.findById(id)
                  .orElseThrow(() -> new ProjectNotFoundException(id));

          return ResponseEntity.ok(
                  new ApiResponse<>(
                          true,
                          "Project details fetched successfully",
                          project
                  )
          );
    }

    // -------------------- POST create project --------------------
    @PostMapping("projects")
    public ResponseEntity<ApiResponse> createProject(@RequestBody Project project) {
    	employeeRepository.findById(project.getEmployeeId()).orElseThrow(() -> new EmployeeNotFoundException(project.getEmployeeId()));
    	Project savedProject = projectRepository.save(project);
    	return ResponseEntity.status(HttpStatus.CREATED)
           .body(new ApiResponse<>(
                   true,
                   "Project created successfully",
                   savedProject
           ));
    }

    // -------------------- PUT update project --------------------
 // -------------------- PUT update project --------------------
    @PutMapping("/projects/{id}")
    public ResponseEntity<ApiResponse> updateProject(
            @PathVariable("id") Long id,
            @RequestBody Project updatedProject) {

        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        if (updatedProject.getProjectName() != null)
            existingProject.setProjectName(updatedProject.getProjectName());
        if (updatedProject.getProjectType() != null)
            existingProject.setProjectType(updatedProject.getProjectType());
        if (updatedProject.getStartingDate() != null)
            existingProject.setStartingDate(updatedProject.getStartingDate());
        if (updatedProject.getEndingDate() != null)
            existingProject.setEndingDate(updatedProject.getEndingDate());
        if (updatedProject.getClientName() != null)
            existingProject.setClientName(updatedProject.getClientName());
        if (updatedProject.getField() != null)
            existingProject.setField(updatedProject.getField());
        if (updatedProject.getEmployeeId() != null)
            existingProject.setEmployeeId(updatedProject.getEmployeeId());

        Project savedProject = projectRepository.save(existingProject);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Project updated successfully",
                        savedProject
                )
        );
    }

    // -------------------- DELETE project --------------------
    @DeleteMapping("projects/{id}")
    public ResponseEntity<ApiResponse> deleteProject(@PathVariable("id") Long id) {
        
        if (!projectRepository.existsById(id)) {
			throw new ProjectNotFoundException(id);
		}
        projectRepository.deleteById(id);
        

		return ResponseEntity.ok(new ApiResponse(true, "Project deleted successfully"));

    }
}
