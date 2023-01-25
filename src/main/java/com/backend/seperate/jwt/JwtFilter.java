package com.backend.seperate.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.backend.seperate.dto.TokenDto;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
/* 이 친구는 지금 안씀 */
public class JwtFilter extends GenericFilterBean {

   private static final String[] whitelist = 
    {"/test","/menu/*","/signIn","signUp","/logout","/css/*"}; //필터 적용을 제외 시킬 리스트를 배열로 만들었다

   private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
   public static final String AUTHORIZATION_ACCESS_HEADER = "Authorization";
   public static final String AUTHORIZATION_REFRESH_HEADER = "refreshToken";
   private TokenProvider tokenProvider;
   public JwtFilter(TokenProvider tokenProvider) {
      this.tokenProvider = tokenProvider;
   }

   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
      System.out.println("::::::::  Jwt Filter doFilter() START :::::::::::");      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
      String requestURI = httpServletRequest.getRequestURI();

      if(!isExcluded(requestURI)){
         System.out.println("SSGGII :::: 검사검사");
         TokenDto tokens = resolveToken(httpServletRequest);
         
         /*
         토큰 종류 : access_token, refresh_token
         조건 : 
               크게 구분 access_token이 있는지 없는지

               1. access_token 없음 or 만료  , refresh_token 없음 or 만료  => ERROR
               2. access_token 있고 사용O    , refresh_token 없음 or 사용X => refresh_token 재발행, 레디스 저장
               3. access_token 있고 사용X    , refresh_token 있고 사용O    => access_token 재발행
               4. access_token 있고 사용O    , refresh_token 있고 사용O    => PASS
         */

         /* 토큰 존재여부 체크 */
         boolean hasAccessToken = StringUtils.hasText(tokens.getAccessToken());
         boolean hasRefreshToken = StringUtils.hasText(tokens.getRefreshToken());

         if( !hasAccessToken && !hasRefreshToken ){
            //Authentication authentication = tokenProvider.getAuthentication(tokens.getAccessToken());
            //logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}",requestURI);
            //httpServletResponse.setStatus(401);
         } else { /* 둘 중 하나라도 존재한다. */
            /* 토큰 사용여부 체크 */
            boolean useableAccessToken = hasAccessToken ? tokenProvider.validateToken(tokens.getAccessToken()) : false;
            boolean useableRefreshToken = hasRefreshToken ? tokenProvider.validateToken(tokens.getRefreshToken()) : false;

            if( !useableAccessToken && !useableRefreshToken ){
               logger.debug("유효한 JWT 토큰이 없습니다, uri: {}",requestURI);
               //httpServletResponse.setStatus(401);
            } else { /* 툴 중 하나라도 사용 가능하다. */
               if( !useableAccessToken ){
                  if( !useableRefreshToken ){
                     logger.debug("유효한 JWT 토큰이 없습니다, uri: {}",requestURI);
                     //httpServletResponse.setStatus(401);
                  }else{
                     Authentication authentication = tokenProvider.getAuthentication(tokens.getRefreshToken());
                     String reissueAccessToken = tokenProvider.makeAccessToken(authentication.getName(), requestURI);
                     
                     /* Access Token 재발급 */
                     httpServletResponse.setHeader(JwtFilter.AUTHORIZATION_ACCESS_HEADER,"Bearer " + reissueAccessToken);

                     logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
                  }
               }
            }
         }
      }
      filterChain.doFilter(servletRequest, servletResponse);


      // if (StringUtils.hasText(tokens.getAccessToken()) && tokenProvider.validateToken(tokens.getAccessToken())) {
         
      // } else {
      //    logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
      // }

      // filterChain.doFilter(servletRequest, servletResponse);
      System.out.println(" :::::::::  Jwt Filter doFilter() END ::::::::::");

   }

   /* Token return */
   private TokenDto resolveToken(HttpServletRequest request) {
      System.out.println("::::: JwtFilter resolveToken START :::::");
      System.out.println("request : "+request);
      String accessToken = request.getHeader(AUTHORIZATION_ACCESS_HEADER);
      String refreshToken = request.getHeader(AUTHORIZATION_REFRESH_HEADER);

      TokenDto tokenDto = new TokenDto();

      /* ACCESS_TOKEN 체크 */
      if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
         tokenDto.setAccessToken(accessToken.substring(7));
      }
      /* REFRESH_TOKEN 체크 */
      if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
         tokenDto.setRefreshToken(refreshToken.substring(7));;
      }
      System.out.println("::::: JwtFilter resolveToken END ::::::");
      return tokenDto;
   }

   /* 화이트 리스트의 경우 인증 체크X */
    private boolean isExcluded(String requestURI) {
      return PatternMatchUtils.simpleMatch(whitelist, requestURI);
   }
}
