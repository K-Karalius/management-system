package com.karalius.webapp.dto;

import java.time.LocalDateTime;

public class ClientDisplay {

    public LocalDateTime appointmentStart;
    public String firstName;
    public String lastName;

    public String serialNumber;

    public ClientDisplay() {
    }

    public ClientDisplay(LocalDateTime appointmentStart, String firstName, String lastName, String serialNumber) {
        this.appointmentStart = appointmentStart;
        this.firstName = firstName;
        this.lastName = lastName;
        this.serialNumber = serialNumber;
    }

    public LocalDateTime getAppointmentStart() {
        return appointmentStart;
    }

    public void setAppointmentStart(LocalDateTime appointmentStart) {
        this.appointmentStart = appointmentStart;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }


}
