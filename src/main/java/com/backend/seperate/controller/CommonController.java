package com.backend.seperate.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommonController {

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        System.out.println("HI");
        return ResponseEntity.ok("hello");
    }
    
}
