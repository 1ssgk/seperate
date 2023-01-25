package com.backend.seperate.service;

import java.util.Collections;

import com.backend.seperate.dto.UserDto;
import com.backend.seperate.entity.Authority;
import com.backend.seperate.entity.User;
import com.backend.seperate.exception.DuplicateMemberException;
import com.backend.seperate.exception.NotFoundMemberException;
import com.backend.seperate.repository.UserRepository;
import com.backend.seperate.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDto signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }
        
        Authority authority = Authority.builder()
                                       .authorityName("ROLE_USER")
                                       .build();

        User user = User.builder()
                        .username(userDto.getUsername())
                        .password(passwordEncoder.encode(userDto.getPassword()))
                        .nickname(userDto.getNickname())
                        .authorities(Collections.singleton(authority))
                        .activated(true)
                        .build();

        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    public Boolean existsByEmail(String email){
      if(userRepository.existsByEmail(email).isPresent()) return true;
      return false;
    }

    public UserDto findUserWithUserName(String username){
        return UserDto.from(userRepository.findByUsername(username).orElse(null));
    }


    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }
}
