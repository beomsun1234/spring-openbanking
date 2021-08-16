package com.bs.openbanking.bank;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${openbank.access-token}")
    private String access_token;
    @Value("${openbank.client-id}")
    private String clientId;
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("clientId", clientId);
        model.addAttribute("access_token",access_token);
        return "/home";
    }


}
