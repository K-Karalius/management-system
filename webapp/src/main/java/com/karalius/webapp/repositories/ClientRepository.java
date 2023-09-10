package com.karalius.webapp.repositories;

import com.karalius.webapp.dto.ClientDisplay;
import com.karalius.webapp.models.Client;
import com.karalius.webapp.models.Specialist;
import com.karalius.webapp.models.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {


    @Query("SELECT nextval('client_sequence')")
    Long getNextClientIdValue();
    @Query("SELECT s FROM client s WHERE s.serialNumber = ?1")
    Client findBySerialNumber(String serialNumber);

    @Query("SELECT s FROM client s WHERE s.mySpecialist = :specialist AND " +
            "(s.status = :firstStatus OR s.status = :secondStatus) AND s.appointmentStart " +
            "= (SELECT max(c.appointmentStart) FROM client c WHERE c.mySpecialist = :specialist AND " +
            "(c.status = :firstStatus OR c.status = :secondStatus))")
    Client findLastClient(
            @Param("specialist") Specialist specialist,
            @Param("firstStatus") AppointmentStatus firstStatus,
            @Param("secondStatus") AppointmentStatus secondStatus
    );

    List<Client> findAllByClientIdGreaterThanAndStatusAndMySpecialist(
            Long id,
            AppointmentStatus status,
            Specialist specialist
    );

    List<Client> findAllByClientIdLessThanAndStatusAndMySpecialist(
            Long id,
            AppointmentStatus status,
            Specialist specialist
    );

    Client findFirstByStatusOrderByClientId(AppointmentStatus status);

    @Query("SELECT NEW com.karalius.webapp.dto.ClientDisplay(c.appointmentStart, c.firstName, c.lastName, c.serialNumber) " +
            "FROM client c " +
            "JOIN c.mySpecialist s " +
            "WHERE s.username = :name AND c.status = 'WAITING' " +
            "ORDER BY c.appointmentStart ASC")
    List<ClientDisplay> findWaitingClientsForSpecialist(@Param("name") String name);

    Client findByStatusAndMySpecialist(AppointmentStatus status, Specialist specialist);
}
