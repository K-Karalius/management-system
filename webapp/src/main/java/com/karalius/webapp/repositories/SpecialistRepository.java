package com.karalius.webapp.repositories;

import com.karalius.webapp.models.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Integer> {
    @Query("SELECT s FROM specialist s WHERE s.fullName = ?1")
    Specialist findByFullName(String fullName);
    @Query("SELECT s.fullName FROM specialist s")
    List<String> getAllSpecialistNames();

    @Query("SELECT s FROM specialist s WHERE s.username = :username AND s.password = :password")
    Specialist findByUsernameAndPassword(
            @Param("username") String username,
            @Param("password") String password
    );
}
