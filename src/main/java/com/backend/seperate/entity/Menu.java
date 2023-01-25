package com.backend.seperate.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`menu`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

  @Id
  @Column(name = "menu_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long menuId;

  @Column(name = "name", length = 50)
  private String name;

  @Column(name = "part", length = 50)
  private String part;

  @Column(name = "activated", nullable = false, columnDefinition = "TINYINT", length = 1)
  private boolean activated;

  @Column(name = "sort")
  private Integer sort;

  @Column(name = "level")
  private Integer level;

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name="path")
  private String path;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns(value = {
    @JoinColumn(name = "parent_id", referencedColumnName = "menu_id", insertable = false, updatable = false)
  })
  private Menu parentMenu;

  @OneToMany(mappedBy = "parentMenu", fetch = FetchType.LAZY)
  private List<Menu> subMenu = new ArrayList<>();

  @Builder
  private Menu(Long menuId, String name, String part, Boolean activated, Integer level, String path, Integer sort,
      Long parentId, Menu parentMenu, List<Menu> subMenu) {
    this.menuId = menuId;
    this.name = name;
    this.part = part;
    this.activated = activated;
    this.level = level;
    this.path = path;
    this.sort = sort;
    this.parentId = parentId;
    this.parentMenu = parentMenu;
    this.subMenu = subMenu;
  }

}
