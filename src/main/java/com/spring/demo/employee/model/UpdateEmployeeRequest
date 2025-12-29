package com.spring.demo.employee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;

public class UpdateEmployeeRequest {

    private String name;
    private String position;

    @JsonProperty("joining_date")
    private String joiningDate;
    
    @JsonProperty("leaving_date")
    private String leavingDate;

    @JsonProperty("company_email")
    @Email(message = "Invalid company email format")
    private String companyEmail;

    @JsonProperty("personal_email")
    @Email(message = "Invalid personal email format")
    private String personalEmail;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String field;

    // ===== Getters & Setters =====

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getLeavingDate() {
        return leavingDate;
    }

    public void setLeavingDate(String leavingDate) {
        this.leavingDate = leavingDate;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
