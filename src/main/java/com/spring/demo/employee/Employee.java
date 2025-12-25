package com.spring.demo.employee;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.spring.demo.project.Project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;


@JsonPropertyOrder({ "id", "name", "field", "position", "joiningDate", "leavingDate", "experience", "personalEmail", "companyEmail", "phone_number", "projects", "projectCount", "createdAt", "updatedAt" }) 
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Employee {

    private static final DateTimeFormatter USER_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private @Id @GeneratedValue Long id;


  @Column(name = "name", nullable = false, unique = true)
  @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
  private String name;
  private String position;

  @Column(name = "joining_date", nullable = false)
  @JsonFormat(pattern = "dd/MM/yyyy", timezone = "UTC")
  @PastOrPresent(message = "Joining date cannot be in the future")
  private Instant joiningDate;

  
  @Column(name = "leaving_date")
  private Instant leavingDate;

  @PositiveOrZero
  private int experience;


  @NotNull(message = "Personal email must not be null")
  @NotEmpty(message = "Personal email must not be empty")
  @Email
  @JsonProperty("personal_email")
  private String personalEmail;

  @NotNull(message = "Company email must not be null")
  @NotEmpty(message = "Company email must not be empty")
  @Email
  @JsonProperty("company_email")
  private String companyEmail;

  @NotNull(message = "Employee field must not be null")
  @NotEmpty(message = "Employee field must not be empty")
  private String field;

  @NotNull(message = "Phone number must not be null")
  @NotEmpty(message = "Phone number must not be empty")
  @JsonProperty("phone_number")
  @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
  private String phoneNumber;
  

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "employeeId", referencedColumnName = "id")
  @JsonIgnoreProperties({ "createdAt", "updatedAt", "employee" })
  private List<Project> projects = new ArrayList<>();
  
  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  @JsonProperty("created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  @JsonProperty("updated_at")
  private Instant updatedAt;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Transient
  @JsonProperty("project_count")
  private Long projectCount; 
  

  Employee() {}

  public Employee(String name, String position, Instant joiningDate, Instant leavingDate,
           int experience, String personalEmail, String companyEmail,
           String field, String phoneNumber, List<Project> projects, Long projectCount) {
    this.name = name;
    this.position = position;
    this.joiningDate = joiningDate;
    this.leavingDate = leavingDate;
    this.experience = experience;
    this.personalEmail = personalEmail;
    this.companyEmail = companyEmail;
    this.field = field;
    this.phoneNumber = phoneNumber;
    this.projects = projects;
    this.projectCount = projectCount;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getPosition() { return position; }
  public void setPosition(String position) { this.position = position; }
  
  public void setJoiningDate(Instant joiningDate) {
	  this.joiningDate = joiningDate;
  }

  public void setLeavingDate(Instant leavingDate) {
	  this.leavingDate = leavingDate;
  }
  
  @JsonSetter("joining_date")
  public void setJoiningDate(String date) {
      LocalDate localDate = LocalDate.parse(date, USER_DATE_FORMAT);
      this.joiningDate = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
  }

  @JsonSetter("leaving_date")
  public void setLeavingDate(String date) {
      if (date == null || date.isBlank()) {
          this.leavingDate = null;
          return;
      }
      LocalDate localDate = LocalDate.parse(date, USER_DATE_FORMAT);
      this.leavingDate = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
  }

  
  @JsonGetter("joining_date")
  public Instant getJoiningDate() {
      return joiningDate;
  }

  @JsonGetter("leaving_date")
  public Instant getLeavingDate() {
      return leavingDate;
  }

  @Transient
  @JsonProperty("experience")
  public String getExperience() {
      if (joiningDate == null) return null;

      LocalDate start = joiningDate.atZone(ZoneOffset.UTC).toLocalDate();
      LocalDate end = (leavingDate != null)
              ? leavingDate.atZone(ZoneOffset.UTC).toLocalDate()
              : LocalDate.now(ZoneOffset.UTC);

      if (end.isBefore(start)) {
          return "0 Year 0 Month 0 day";
      }

      Period period = Period.between(start, end);

      return String.format(
              "%d Year %d Month %d day",
              period.getYears(),
              period.getMonths(),
              period.getDays()
      );
  }


  public String getPersonalEmail() { return personalEmail; }
  public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

  public String getCompanyEmail() { return companyEmail; }
  public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }

  public String getField() { return field; }
  public void setField(String field) { this.field = field; }

  public String getPhoneNumber() { return phoneNumber; }
  public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
  

  
  public List<Project> getProjects() {
      return projects;
  }

  public void setProjects(List<Project> projects) {
      this.projects = projects;
  }
  
  public Long getProjectCount() {
	  return projectCount;
  }
  
  public void setProjectCount(Long projectCount) {
	  this.projectCount = projectCount;
  }
  
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
    if (!(o instanceof Employee)) return false;
    Employee employee = (Employee) o;
    return Objects.equals(id, employee.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Employee{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", position='" + position + '\'' +
        ", field='" + field + '\'' +
        '}';
  }
}
