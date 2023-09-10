package com.karalius.webapp.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "client")
@Table(name = "client")
public class Client {

    @Id
    @SequenceGenerator(
            name = "client_sequence",
            sequenceName = "client_sequence",
            allocationSize = 1
    )

//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "client_sequence"
//            )

    @Column(name = "client_id")
    private Long clientId;

    @Column(
            name = "first_name",
            columnDefinition = "TEXT"
    )
    private String firstName;

    @Column(
            name = "last_name",
            columnDefinition = "TEXT"
    )
    private String lastName;

    @Column(
            name = "serial_number",
            unique = true,
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String serialNumber;

    @Column(
            name = "appointment_start"
            //unique = true                   //change later maybe
    )
    private LocalDateTime appointmentStart;

//    @Column(name = "appointment_end")
//    private LocalDateTime appointmentEnd;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialist_id")
    private Specialist mySpecialist;


    public Client() {
        status = AppointmentStatus.WAITING;
    }
    public Client(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        status = AppointmentStatus.WAITING;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

    public LocalDateTime getAppointmentStart() {
        return appointmentStart;
    }

    public void setAppointmentStart(LocalDateTime appointmentStart) {
        this.appointmentStart = appointmentStart;
    }

//    public LocalDateTime getAppointmentEnd() {
//        return appointmentEnd;
//    }
//
//    public void setAppointmentEnd(LocalDateTime appointmentEnd) {
//        this.appointmentEnd = appointmentEnd;
//    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public Specialist getMySpecialist() {
        return mySpecialist;
    }

    public void setMySpecialist(Specialist mySpecialist) {
        this.mySpecialist = mySpecialist;
    }

    /*

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    */

}
