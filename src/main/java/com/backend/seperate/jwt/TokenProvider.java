package com.backend.seperate.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.backend.seperate.dto.TokenDto;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenProvider implements InitializingBean {

   private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
   private static final String ACCESS_TOKEN_KEY = "access_token";
   private static final String REFRESH_TOKEN_KEY = "refresh_token";
   private final String secret;
   
   private Key key;

   private final long accessTokenValidityInMilliseconds;
   private final long refreshTokenValidityInMilliseconds;

   public TokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
      this.secret = secret;
      this.accessTokenValidityInMilliseconds = tokenValidityInSeconds * 2 * 1000;
      this.refreshTokenValidityInMilliseconds = tokenValidityInSeconds * 24 * 14 * 1000;
   }

   @Override
   public void afterPropertiesSet() {
      byte[] keyBytes = Decoders.BASE64.decode(secret);
      this.key = Keys.hmacShaKeyFor(keyBytes);
   }

   /* Access Token 생성 (2시간) */
   public String makeAccessToken (String username, String authorities){
      long now = (new Date()).getTime();
      Date validity = new Date(now + this.accessTokenValidityInMilliseconds);
      
      return Jwts
         .builder()
         .setSubject(username)
         .claim("roles", authorities)
         .signWith(key, SignatureAlgorithm.HS512)
         .setExpiration(validity)
         .compact();
   }
   
   /* Refresh Token 생성 (2주) */
   public String makeRefreshToken (String username, String authorities){
      long now = (new Date()).getTime();
      Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);
      
      return Jwts
         .builder()
         .setSubject(username)
         .claim("roles", authorities)
         .signWith(key, SignatureAlgorithm.HS512)
         .setExpiration(validity)
         .compact();
   }

   /* Token 생성 (Access & Refresh) */
   public TokenDto createToken(Authentication authentication, HttpServletResponse response) {
      String authorities = authentication.getAuthorities().stream()
         .map(GrantedAuthority::getAuthority)
         .collect(Collectors.joining(","));

      String userName = authentication.getName();

      TokenDto tokenDto = new TokenDto();
      tokenDto.setAccessToken(makeAccessToken(userName,authorities));



      //tokenDto.setRefreshToken(makeRefreshToken(userName,authorities));

      Cookie cookie = new Cookie(JwtFilter.AUTHORIZATION_REFRESH_HEADER,makeRefreshToken(userName,authorities));
      cookie.setSecure(true); 
      cookie.setHttpOnly(true); // 브라우저만 cookie 정보를 읽을 수 있음. 자바스크립트나 다른 코드에서 불가능.
      cookie.setPath("/"); // 지정 경로부터 하위에 Cookie를 저장
      cookie.setMaxAge(60 * 60 * 24 * 14); // refreshToken과 동일하게 2주로 설정
      response.addCookie(cookie);

      return tokenDto;
   }

   public Authentication getAuthentication(String token) {
      Claims claims = Jwts
              .parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody();

      Collection<? extends GrantedAuthority> authorities =
         Arrays.stream(claims.get(REFRESH_TOKEN_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

      User principal = new User(claims.getSubject(), "", authorities);

      return new UsernamePasswordAuthenticationToken(principal, token, authorities);
   }

   /* 토큰 인증 */
   public boolean validateToken(String token) {
      try {
         Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
         return true;
      } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
         logger.info("잘못된 JWT 서명입니다.");
      } catch (ExpiredJwtException e) {
         logger.info("만료된 JWT 토큰입니다.");
      } catch (UnsupportedJwtException e) {
         logger.info("지원되지 않는 JWT 토큰입니다.");
      } catch (IllegalArgumentException e) {
         logger.info("JWT 토큰이 잘못되었습니다.");
      }
      return false;
   }
}
