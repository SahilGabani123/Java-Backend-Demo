package com.spring.demo.project;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.spring.demo.employee.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
@JsonPropertyOrder({ "id", "employeeId", "projectName", "projectType", "startingDate", "endingDate", "clientName", "employee", "field" }) 
@EntityListeners(AuditingEntityListener.class)
public class Project {

  private @Id @GeneratedValue Long id;


  @JsonProperty("project_name")
  private String projectName;

  @JsonProperty("project_type")
  private String projectType;

  @JsonProperty("starting_date")
  private LocalDate startingDate;

  @JsonProperty("ending_date")
  private LocalDate endingDate;
  
  @JsonProperty("client_name")
  private String clientName;
  
  private String field; // android / ios / web / all

  @JsonProperty("employee_id")
  private Long employeeId; // ✅ Added
  

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
  


  @JsonInclude(JsonInclude.Include.NON_NULL)
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id", referencedColumnName = "employeeId")
  @JsonIgnoreProperties({ "created_at", "updated_at", "projects", "experience", "personal_email", "joining_date", "leaving_date" })
  private Employee employee;

  Project() {}

  Project(String projectName, String projectType, LocalDate startingDate,
          LocalDate endingDate, String clientName, String field, Long employeeId,Employee employee) {
    this.projectName = projectName;
    this.projectType = projectType;
    this.startingDate = startingDate;
    this.endingDate = endingDate;
    this.clientName = clientName;
    this.field = field;
    this.employeeId = employeeId;
    this.employee = employee;
  }

  // Getters and Setters

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getProjectName() { return projectName; }
  public void setProjectName(String projectName) { this.projectName = projectName; }

  public String getProjectType() { return projectType; }
  public void setProjectType(String projectType) { this.projectType = projectType; }

  public LocalDate getStartingDate() { return startingDate; }
  public void setStartingDate(LocalDate startingDate) { this.startingDate = startingDate; }

  public LocalDate getEndingDate() { return endingDate; }
  public void setEndingDate(LocalDate endingDate) { this.endingDate = endingDate; }

  public String getClientName() { return clientName; }
  public void setClientName(String clientName) { this.clientName = clientName; }

  public String getField() { return field; }
  public void setField(String field) { this.field = field; }

  public Long getEmployeeId() { return employeeId; }
  public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
  
  public Employee getEmployee() { return employee; }
  public void setEmployee(Employee employee) { this.employee = employee; }
  
  public Instant getCreatedAt() {
      return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
      this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
      return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
      this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Project)) return false;
    Project project = (Project) o;
    return Objects.equals(id, project.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
