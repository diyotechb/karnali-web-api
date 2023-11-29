package com.example.starter.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class BackendController {
    @GetMapping("/now")
    public String getCurrentTimeUsingDate() {
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);
        return "Current Date and Time: " + formattedDate;
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "Hello Admin!";
    }

    @GetMapping("/user")
    public String getUser() {
        return "Hello User!";
    }
}
