//package com.karalius.webapp;
//
//import com.karalius.webapp.models.Specialist;
//import com.karalius.webapp.repositories.SpecialistRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Configuration
//public class Config {
//
//    @Bean
//    CommandLineRunner commandLineRunner(SpecialistRepository repository){
//        return args -> {
//            Specialist specialist1 = new Specialist(
//                    "karalius123",
//                    "Kristupas Karalius",
//                    "llalal123"
//            );
//
//            Specialist specialist2 = new Specialist(
//                    "dantistas123",
//                    "Aleksas Matilaitis",
//                    "12345"
//            );
//
//            repository.saveAll(
//                    List.of(specialist1, specialist2)
//            );
//        };
//    }
//}
