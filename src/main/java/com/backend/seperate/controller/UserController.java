package com.backend.seperate.controller;

import com.backend.seperate.dto.LoginDto;
import com.backend.seperate.dto.TokenDto;
import com.backend.seperate.dto.UserDto;
import com.backend.seperate.jwt.JwtFilter;
import com.backend.seperate.jwt.TokenProvider;
import com.backend.seperate.service.UserService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/")
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public UserController(UserService userService, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        System.out.println("HI");
        return ResponseEntity.ok("hello");
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }

    @PostMapping("/test-redirect")
    public void testRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/user");
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(
            @Valid @RequestBody UserDto userDto
    ) {
        return ResponseEntity.ok(userService.signup(userDto));
    }

    /* ????????? ????????? 
     * JWT Token ???????????? ????????? ????????? ????????? ??? ??? ??????.
     * 1. ???????????? username,password ??? ????????? ????????? ????????? ????????????.
     * 2. DB ?????? ??? ????????? ????????? ???????????? Access & Refresh Token??? ???????????????. Refresh Token??? DB??? ?????? ????????????.(?????? ?????? ??????)
     * 3. ????????? ????????? TokenDto ????????? ?????? Response Body??? ????????? ???????????????. (Local Storage ??????  IndexedDB??? ?????? ??????)
    */
    @PostMapping("/signIn")
    public ResponseEntity<TokenDto> signIn(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request){
        System.out.println(":::::::::::: AuthController signin START ::::::::::");
        HttpHeaders httpHeaders = new HttpHeaders();
        
        /* ????????? ?????????(?????? or e-mail)??? ????????? ?????? */
        UserDto userDto = userService.findUserWithUserName(loginDto.getUsername());
        if( !StringUtils.hasText(userDto.getUsername()) ){
            return new ResponseEntity<>(new TokenDto(), httpHeaders, HttpStatus.UNAUTHORIZED);
        }else if( userDto.isActivated() ){
            return new ResponseEntity<>(new TokenDto(), httpHeaders, HttpStatus.UNAUTHORIZED);
        }
        
        System.out.println("@@@@ /signIn @@@@");
		String ip = request.getHeader("X-FORWARDED-FOR");
        System.out.println(" ip : "+ ip);
        System.out.println(" request.getRemoteAddr() : "+ request.getRemoteAddr());


        /* ????????? ????????? ??????????????? authenticationToken ?????? */
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = tokenProvider.createToken(authentication);
        httpHeaders.add(JwtFilter.AUTHORIZATION_ACCESS_HEADER, "Bearer " + tokenDto.getAccessToken());
        httpHeaders.add(JwtFilter.AUTHORIZATION_REFRESH_HEADER, "Bearer " + tokenDto.getRefreshToken());

        /* ????????? ????????? ?????? ??? Refresh Token ?????? */


        System.out.println("httpHeaders : "+httpHeaders);
        System.out.println(":::::::::: AuthController signIn END ::::::::::::::");

        return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserDto> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getMyUserWithAuthorities());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username));
    }
}
