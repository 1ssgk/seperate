package com.backend.seperate.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommonController {
    
    @GetMapping("/test")
    public String testApi (HttpServletRequest request,HttpServletResponse response) {
        System.out.println("SSGGII :::: testApi"); 
        return "test";
    }
}
