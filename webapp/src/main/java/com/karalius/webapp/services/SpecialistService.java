package com.karalius.webapp.services;

import com.karalius.webapp.dto.ClientDisplay;
import com.karalius.webapp.dto.SpecialistSignIn;
import com.karalius.webapp.models.AppointmentStatus;
import com.karalius.webapp.models.Client;
import com.karalius.webapp.models.Specialist;
import com.karalius.webapp.repositories.ClientRepository;
import com.karalius.webapp.repositories.SpecialistRepository;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final ClientRepository clientRepository;

    private final long appointmentLengthInMinutes = 5;

    @Autowired
    public SpecialistService(SpecialistRepository specialistRepository, ClientRepository clientRepository) {
        this.specialistRepository = specialistRepository;
        this.clientRepository = clientRepository;
    }
    public ResponseEntity<String> signIn(SpecialistSignIn specialistSignIn) {

        Specialist specialist = specialistRepository.findByUsernameAndPassword(
                specialistSignIn.getUsername(),
                specialistSignIn.getPassword()
        );

        if(specialist == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("null");
        }

        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    public List<ClientDisplay> getSpecialistClients(String username) {
        List<ClientDisplay> clientsToDisplay = clientRepository.findWaitingClientsForSpecialist(username);

        if(clientsToDisplay == null){
            return new ArrayList<>();

        }
        return clientsToDisplay;
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public String startStopMeeting(String serialNumber) {
        Client client = clientRepository.findBySerialNumber(serialNumber);
        String message;

        if(client == null){
            return "Client does not exists";
        }

        Client attendingClient;
        if(client.getStatus() == AppointmentStatus.ATTENDING){
            attendingClient = client;
        }else{
            attendingClient = clientRepository.findByStatusAndMySpecialist(
                    AppointmentStatus.ATTENDING,
                    client.getMySpecialist()
            );
        }
        if(attendingClient == null){
            message = startMeeting(client);
        }else{
            message = stopMeeting(attendingClient);
        }

        return message;
    }

    private String stopMeeting(Client client){
        client.setStatus(AppointmentStatus.VISITED);
        return "STOPPED";
    }


    private String startMeeting(Client client){
        advanceOrPostpone(client, true);
        client.setStatus(AppointmentStatus.ATTENDING);
        client.setAppointmentStart(null);
        return "STARTED";
    }

    private void advanceOrPostpone(Client client, boolean startOrEnd){          //start = 1, end = 0
        Client firstClient = clientRepository.findFirstByStatusOrderByClientId(AppointmentStatus.WAITING);
        long minuteDifference;
        if(startOrEnd){
            minuteDifference = ChronoUnit.MINUTES.between(LocalDateTime.now(), firstClient.getAppointmentStart());
        }else{
            minuteDifference = ChronoUnit.MINUTES.between(
                    LocalDateTime.now(),
                    firstClient.getAppointmentStart().plusMinutes(appointmentLengthInMinutes)
            );
        }

        // all clients after the provided client, not optimal for huge list though
        List<Client> afterList = clientRepository.findAllByClientIdGreaterThanAndStatusAndMySpecialist(
                client.getClientId(),
                client.getStatus(),
                client.getMySpecialist()
        );

        // all clients before, not optimal for huge list though
        List<Client> beforeList = clientRepository.findAllByClientIdLessThanAndStatusAndMySpecialist(
                client.getClientId(),
                client.getStatus(),
                client.getMySpecialist()
        );

        if(minuteDifference >= 0){
            if(appointmentLengthInMinutes - minuteDifference >= 0){
                postponeMeetings(beforeList, appointmentLengthInMinutes - minuteDifference);
            }else{
                advanceMeetings(beforeList, Math.abs(appointmentLengthInMinutes - minuteDifference));
            }
            advanceMeetings(afterList, minuteDifference);

        }else{
            minuteDifference *= -1;
            postponeMeetings(beforeList, minuteDifference + appointmentLengthInMinutes);
            postponeMeetings(afterList, minuteDifference);
        }


    }

    private void postponeMeetings(List<Client> clientsToPostpone, long timeDiff){
        if(clientsToPostpone != null){
            for(Client c : clientsToPostpone){
                c.setAppointmentStart(c.getAppointmentStart().plusMinutes(timeDiff));
            }
        }
    }

    private void advanceMeetings(List<Client> clientsToAdvance, long timeDiff){
        if(clientsToAdvance != null){
            for(Client c : clientsToAdvance){
                c.setAppointmentStart(c.getAppointmentStart().minusMinutes(timeDiff));
            }
        }
    }
}
