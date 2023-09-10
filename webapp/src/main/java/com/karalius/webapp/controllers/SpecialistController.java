package com.karalius.webapp.controllers;

import com.karalius.webapp.dto.ClientDisplay;
import com.karalius.webapp.dto.SpecialistSignIn;
import com.karalius.webapp.services.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = "/")
public class SpecialistController {

    private final SpecialistService specialistService;

    @Autowired
    public SpecialistController(SpecialistService specialistService){
        this.specialistService = specialistService;
    }

    @GetMapping("/login")
    public String showLogIn(Model model){
        model.addAttribute("specialistIn", new SpecialistSignIn());
        return "login";
    }

    @PostMapping("/login/submit")
    public String signIn(@ModelAttribute SpecialistSignIn specialistIn, Model model){
        ResponseEntity<String> response = specialistService.signIn(specialistIn);
        if(response.getStatusCode().is2xxSuccessful()){
            List<ClientDisplay> clientDisplayList = specialistService.getSpecialistClients(specialistIn.getUsername());
            model.addAttribute("clientDisplay", clientDisplayList);
            model.addAttribute("usernameIn", specialistIn.getUsername());
            return "specialistPage";
        }else{
            return "login";
        }
    }

    @PutMapping("/specialistPage/updateClient")
    public String startStopMeeting(
            @ModelAttribute("selectedSerialNumber") String selectedSerial,
            @ModelAttribute("usernameIn") String userName,
            Model model
    ){
        String message = specialistService.startStopMeeting(selectedSerial);

        if(message.equals("STARTED")){
            model.addAttribute("startedAppointment", selectedSerial);
        }else if(message.equals("STOPPED")){
            model.addAttribute("startedAppointment", "NONE");
        }else{
            model.addAttribute("startedAppointment", message);
        }
        List<ClientDisplay> clientDisplayList = specialistService.getSpecialistClients(userName);
        model.addAttribute("clientDisplay", clientDisplayList);
        return "specialistPage";
    }

}
