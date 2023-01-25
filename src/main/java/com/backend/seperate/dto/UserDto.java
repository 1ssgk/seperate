package com.backend.seperate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import com.backend.seperate.entity.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {

  public UserDto(){}

   @NotNull
   @Size(min = 3, max = 50)
   private String username;

   @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
   @NotNull
   @Size(min = 3, max = 100)
   private String password;

   @NotNull
   @Size(min = 3, max = 50)
   private String nickname;

   private String email;

   private boolean activated;

   private String accessToken;

   private Set<AuthorityDto> authorityDtoSet;

   /* 유저의 전체 정보가 필요한 경우 반환하는 데이터형태 */
   public static UserDto from(User user) {
      if(user == null) return null;

      return UserDto.builder()
              .username(user.getUsername())
              .nickname(user.getNickname())
              .activated(user.isActivated())
              .email(user.getEmail())
              .authorityDtoSet(user.getAuthorities().stream()
                      .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
                      .collect(Collectors.toSet()))
              .build();
   }

   /* 로그인 하는 경우 반환하는 데이터형태 */
   public static UserDto fromSignin(UserDto user){
    return UserDto.builder()
            .username(user.getUsername())
            .nickname(user.getNickname())
            .authorityDtoSet(user.getAuthorityDtoSet())
            .build();
   }
}