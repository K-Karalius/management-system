package com.karalius.webapp.models;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "specialist")
@Table(name = "specialist")
public class Specialist {

    @Id
    @SequenceGenerator(
            name = "specialist_seq",
            sequenceName = "specialist_seq",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "specialist_seq"
    )

    private Long id;

    @Column(
            name = "username",
            columnDefinition = "TEXT",
            unique = true
    )
    private String username;

    @Column(
            columnDefinition = "TEXT"
    )
    private String password;
    @Column(
            name = "full_name",
            columnDefinition = "TEXT",
            unique = true               // assuming there will be no specialist with the same name
    )
    private String fullName;

    @OneToMany(
            mappedBy = "mySpecialist",
            fetch = FetchType.LAZY)
    private List<Client> clientList;


    public Specialist() {
    }

    public Specialist(String username, String fullName) {
        clientList = new ArrayList<>();
        this.username = username;
        this.fullName = fullName;
        //this.password = password; // delete later, for testing only
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

}
