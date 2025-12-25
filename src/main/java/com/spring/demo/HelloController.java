package com.spring.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Greetings from Sahil Gabani!";
    }
    

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome from Sahil Gabani!";
    }
    
    @GetMapping("/actuator/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Welcome from Sahil Gabani!");
        return response;
    }


}
