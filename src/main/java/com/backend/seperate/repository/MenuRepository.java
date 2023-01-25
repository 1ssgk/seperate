package com.backend.seperate.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.seperate.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
  boolean existsByParentId(Long parentId);

  List<Menu> findAllByLevelAndPart(Integer level, String part, Sort sort);
}
