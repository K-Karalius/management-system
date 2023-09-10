package com.karalius.webapp.controllers;

import com.karalius.webapp.dto.ClientRequest;
import com.karalius.webapp.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(path = "/")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping()
    public String AppointmentPage(Model model) {
        List<String> specialists = clientService.getSpecialists();
        model.addAttribute("specialists", specialists);
        model.addAttribute("clientRequest", new ClientRequest());
        return "homepage";
    }

    @PostMapping("/create")
    public String addClient(@ModelAttribute ClientRequest clientRequest, RedirectAttributes redirectAttributes) {
        String serialNumber = clientService.addClient(clientRequest);
        redirectAttributes.addFlashAttribute("serialNumber", serialNumber);
        return "redirect:/generatedID";
    }

    @GetMapping("/generatedID")
    public String showID(@ModelAttribute("serialNumber") String num, Model model){
        model.addAttribute("serialNumber", num);
        return "generatedID";
    }

    @GetMapping("/check")
    public String redirectToCheck(){
        return "check";
    }
    @GetMapping("/cancel")
    public String redirectToCancel(){
        return "cancel";
    }

    @PostMapping("/check/submit")
    public String checkTime(@RequestParam("checkSerial") String serialNum, Model model){
        String message = clientService.getTimeLeft(serialNum);
        model.addAttribute("checkMessage", message);
        return "check";
    }

    @PutMapping ("/cancel/submit")
    public String cancel(@RequestParam("cancelSerial") String serialNum, Model model){
        String message = clientService.cancelAppointment(serialNum);
        model.addAttribute("cancelMessage", message);
        return "cancel";
    }

}
