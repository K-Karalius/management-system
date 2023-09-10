package com.karalius.webapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientRequest {

    private String firstName;
    private String lastName;
    private String specialistFullName;

    public ClientRequest(){

    }
    public ClientRequest(String firstName, String lastName, String specialistFullName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialistFullName = specialistFullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialistFullName() {
        return specialistFullName;
    }

    public void setSpecialistFullName(String specialistFullName) {
        this.specialistFullName = specialistFullName;
    }

}
