package com.karalius.webapp.services;

import com.karalius.webapp.dto.ClientRequest;
import com.karalius.webapp.models.Client;
import com.karalius.webapp.models.Specialist;
import com.karalius.webapp.models.AppointmentStatus;
import com.karalius.webapp.repositories.ClientRepository;
import com.karalius.webapp.repositories.SpecialistRepository;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class ClientService {


    public final ClientRepository clientRepository;
    public final SpecialistRepository specialistRepository;
    private final long appointmentLengthInMinutes = 5;

    @Autowired
    public ClientService(ClientRepository clientRepository, SpecialistRepository specialistRepository) {
        this.clientRepository = clientRepository;
        this.specialistRepository = specialistRepository;
    }
    @Lock(LockModeType.PESSIMISTIC_READ)
    public synchronized String addClient(ClientRequest clientRequest){

        Specialist specialist = specialistRepository.findByFullName(clientRequest.getSpecialistFullName());
        if(specialist == null){
                throw new IllegalStateException(
                        "Specialist with the name " + clientRequest.getSpecialistFullName() + " not found"
               );
        }

        Client client = setUpNewClient(clientRequest, specialist);
        clientRepository.save(client);
        return client.getSerialNumber();

    }

    private Client setUpNewClient(ClientRequest clientRequest, Specialist specialist){
        String firstName = clientRequest.getFirstName().trim();
        String lastName = clientRequest.getLastName().trim();

        Client client = new Client(firstName, lastName);
        client.setMySpecialist(specialist);

        Long clientId = clientRepository.getNextClientIdValue();
        client.setClientId(clientId);

        String serialNumber = generateSerialNumber(client.getFirstName(), client.getLastName(), clientId);
        client.setSerialNumber(serialNumber);

        Client lastClient = clientRepository.findLastClient(specialist, AppointmentStatus.WAITING, AppointmentStatus.ATTENDING);

        if(lastClient == null){
            client.setAppointmentStart(LocalDateTime.now());
        }else{
            client.setAppointmentStart(lastClient.getAppointmentStart().plusMinutes(appointmentLengthInMinutes));
        }
        specialist.getClientList().add(client);

        return client;
    }

    private String generateSerialNumber(String firstName, String lastName, Long clientId){
        String result = "";
        int substringLength = 3;

        firstName = firstName.replace(" ", "");
        lastName = lastName.replace(" ", "");

        result += getSubstring(firstName, substringLength);
        result += getSubstring(lastName, substringLength);
        result += clientId.toString();

        return result;
    }

    private String getSubstring(String text, int substringLength){
        if(text.length() >= substringLength){
            return text.substring(0, substringLength);
        }else{
            return text;
        }
    }

    public List<String> getSpecialists() {
        return specialistRepository.getAllSpecialistNames();
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public String cancelAppointment(String serialNum) {
        Client client = clientRepository.findBySerialNumber(serialNum.trim());

        if(client == null){
            return "Appointment by the serial number " + serialNum + " does NOT exist";
        }
        if (client.getStatus() != AppointmentStatus.WAITING){
            return "Appointment status: " + client.getStatus().toString();
        }

        // not optimal for large data lists, using batch update might be better
        List<Client> clientsToUpdate = clientRepository.findAllByClientIdGreaterThanAndStatusAndMySpecialist(
                client.getClientId(),
                AppointmentStatus.WAITING,
                client.getMySpecialist()
        );

        if(clientsToUpdate != null){
            for(Client c : clientsToUpdate){
                LocalDateTime newTime = c.getAppointmentStart().minusMinutes(appointmentLengthInMinutes);
                c.setAppointmentStart(newTime);
            }
        }

        client.setAppointmentStart(null);
        client.setStatus(AppointmentStatus.CANCELED);
        return "Appointment canceled successfully";
    }

    public String getTimeLeft(String serialNum) {
        Client client = clientRepository.findBySerialNumber(serialNum.trim());

        if(client == null){
            return "Appointment by the serial number " + serialNum + " does NOT exist";
        }
        if(client.getStatus() != AppointmentStatus.WAITING){
            return "Appointment status: " + client.getStatus().toString();
        }

        LocalDateTime appointment = client.getAppointmentStart();
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), appointment);
        if(minutes >= 0){
            long hours = minutes / 60;
            minutes = minutes % 60;

            return  hours + " hour(s) and " +  minutes + " minute(s) left";
        }

        return "soon...";

    }
}
