package com.backend.seperate.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.seperate.dto.LoginDto;
import com.backend.seperate.dto.TokenDto;
import com.backend.seperate.jwt.JwtFilter;
import com.backend.seperate.jwt.TokenProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    /* 사용자 로그인 
     * JWT Token 관련해서 제재를 가하지 않아도 될 듯 하다.
     * 1. 전달받은 username,password 로 사용자 정보가 있는지 확인한다.
     * 2. DB 조회 후 사용이 가능한 유저라면 Access & Refresh Token을 발행해준다. Refresh Token은 DB에 따로 저장한다.(추후 비교 목적)
     * 3. 발행한 토큰을 TokenDto 객체로 생성 Response Body에 담아서 전달해준다. (Local Storage 또는  IndexedDB에 보관 목적)
    */
    // @PostMapping("/signIn")
    // public ResponseEntity<TokenDto> signin(@Valid @RequestBody LoginDto loginDto){
    //     System.out.println(":::::::::::: AuthController signin START ::::::::::");

    //     UserService.getUserWithUserName(loginDto.getUsername());//getUserWithUserName

    //     /* 유저의 이름과 비밀번호로 authenticationToken 생성 */
    //     UsernamePasswordAuthenticationToken authenticationToken =
    //             new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());



    //     Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    //     SecurityContextHolder.getContext().setAuthentication(authentication);

    //     TokenDto tokenDto = tokenProvider.createToken(authentication);
    //     String accessToken = tokenDto.getAccessToken();
    //     String refreshToken = tokenDto.getRefreshToken();

    //     HttpHeaders httpHeaders = new HttpHeaders();
    //     httpHeaders.add(JwtFilter.AUTHORIZATION_ACCESS_HEADER, "Bearer " + accessToken);
    //     httpHeaders.add(JwtFilter.AUTHORIZATION_REFRESH_HEADER, "Bearer " + refreshToken);

    //     System.out.println("httpHeaders : "+httpHeaders);
    //     System.out.println(":::::::::: AuthController signIn END ::::::::::::::");

    //     return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
    // }

    /* 권한 체크 테스트 로직 */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {

        System.out.println(":::::::::::: AuthController authorize START ::::::::::");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = tokenProvider.createToken(authentication,response);
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_ACCESS_HEADER, "Bearer " + accessToken);
        httpHeaders.add(JwtFilter.AUTHORIZATION_REFRESH_HEADER, "Bearer " + refreshToken);

        System.out.println("httpHeaders : "+httpHeaders);

        System.out.println(":::::::::: AuthController authorize END ::::::::::::::");

        return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
    }
}
