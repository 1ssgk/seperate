package com.backend.seperate.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.backend.seperate.dto.MenuDto;
import com.backend.seperate.repository.MenuRepository;

@Service
public class MenuService {
  private final MenuRepository menuRepository;

  public MenuService(MenuRepository menuRepository) {
    this.menuRepository = menuRepository;
  }

  @Transactional
  public List<MenuDto> findAllMenuByLevelAndPart(String part) {
    return menuRepository
        .findAllByLevelAndPart(0, part, Sort.by(Sort.Direction.ASC, "sort"))
        .stream()
        .map(MenuDto::from)
        .collect(Collectors.toList());
  }
}
