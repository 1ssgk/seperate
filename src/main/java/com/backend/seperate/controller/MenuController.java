package com.backend.seperate.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.backend.seperate.dto.MenuDto;
import com.backend.seperate.service.MenuService;

@RestController
@RequestMapping("/menu")
public class MenuController {


  private final MenuService menuService;
  private static final Logger log = LoggerFactory.getLogger(MenuController.class);
  
  public MenuController(MenuService menuService){
    this.menuService = menuService;
  }

  /* 일반적인 유저가 사용하는 메뉴를 가져오는데 사용하는 Get요청 */
  @GetMapping("/getMenu")
  @ResponseBody
    public ResponseEntity <List<MenuDto>> findAllHomeMenu(){
      return ResponseEntity.ok(menuService.findAllMenuByLevelAndPart("home"));
    }
}
