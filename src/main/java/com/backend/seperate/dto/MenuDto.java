package com.backend.seperate.dto;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import com.backend.seperate.entity.Menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuDto {

  @Size(min = 3, max = 50)
  private Long menuId;

  @Size(min = 3, max = 50)
  private String name;

  @Size(min = 1, max = 1)
  private boolean activated;

  private Integer level;

  private String path;

  private Integer sort;

  private Long parentId;

  private List<MenuDto> subMenu;

  private String part;

  private String rootCheck;

  /* JPA에서 사용할 수 있게 static 으로 */
  public static MenuDto from(Menu menu) {
    if(menu == null) return null;
   
    String isRoot = menu.getLevel() == 0 ? "root" : "sub";

    return MenuDto.builder()
      .menuId(menu.getMenuId())
      .name(menu.getName())
      .activated(menu.isActivated())
      .sort(menu.getSort())
      .level(menu.getLevel())
      .path(menu.getPath())
      .rootCheck(isRoot)
      .parentId(menu.getParentId())
      .subMenu(menu.getSubMenu().stream().map(MenuDto::from).collect(Collectors.toList()))
      .build();
  }
}
