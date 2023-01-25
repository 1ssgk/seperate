package com.backend.seperate.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.seperate.dto.MenuDto;
import com.backend.seperate.service.MenuService;

@RestController
@RequestMapping("/common")
public class CommonController {
  private final MenuService menuService;

  public CommonController(MenuService menuService){
    this.menuService = menuService;
  }

  @PostMapping("/test")
  @ResponseBody
    public ResponseEntity <List<MenuDto>> findAllHomeMenu(@RequestParam(value = "part", required = true) String part){
      System.out.println("TEST 찍힌다고??");
        return ResponseEntity.ok(menuService.findAllMenuByLevelAndPart(part));
    }

    
    
}
