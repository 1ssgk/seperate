package com.backend.seperate.interceptors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.backend.seperate.dto.TokenDto;
import com.backend.seperate.jwt.TokenProvider;

@Component
public class JwtInterceptor implements HandlerInterceptor {

  private static final String[] whitelist = { "/test", "/menu/*", "/sign/*", "/signIn", "signUp", "/logout", "/css/*" }; // 필터 적용을
                                                                                                              // 제외 시킬
                                                                                                              // 리스트를
  // 배열로 만들었다

  private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);
  public static final String AUTHORIZATION_ACCESS_HEADER = "Authorization";
  public static final String AUTHORIZATION_REFRESH_HEADER = "refreshToken";
  private TokenProvider tokenProvider;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    /*
     * 클라이언트의 요청을 컨트롤러에 전달하기 전에 호출된다.
     * 여기서 false를 리턴하면 다음 내용(Controller)을 실행하지 않는다.
     * 
     * 여기서 jwt 관련 인증을 하면 될 것 같다.
     */
    String requestURI = request.getRequestURI();
    System.out.println("##WHERE JwtInterceptor preHandle");
    System.out.println("##WHERE JwtInterceptor preHandle.requestURI :" + requestURI);

    if (!isExcluded(requestURI)) {
      System.out.println("##WHERE 여길 쳐 탔다고?");
      TokenDto tokens = resolveToken(request);

      /*
       * 토큰 종류 : access_token, refresh_token
       * 조건 :
       * 크게 구분 access_token이 있는지 없는지
       * 
       * 1. access_token 없음 or 만료 , refresh_token 없음 or 만료 => ERROR
       * 2. access_token 있고 사용O , refresh_token 없음 or 사용X => 사용은 가능하나 refresh_Token이
       * 없기에 곧 재로그인 해야할듯
       * 3. access_token 있고 사용X , refresh_token 있고 사용O => access_token 재발행
       * 4. access_token 있고 사용O , refresh_token 있고 사용O => PASS
       */

      /* 토큰 존재여부 체크 */
      boolean hasAccessToken = StringUtils.hasText(tokens.getAccessToken());
      boolean hasRefreshToken = StringUtils.hasText(tokens.getRefreshToken());

      if (!hasAccessToken && !hasRefreshToken) {
        // Authentication authentication =
        // tokenProvider.getAuthentication(tokens.getAccessToken());
        // logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}",
        // authentication.getName(), requestURI);
        logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        // httpServletResponse.setStatus(401);
      } else { /* 둘 중 하나라도 존재한다. */
        /* 토큰 사용여부 체크 */
        boolean useableAccessToken = hasAccessToken ? tokenProvider.validateToken(tokens.getAccessToken()) : false;
        boolean useableRefreshToken = hasRefreshToken ? tokenProvider.validateToken(tokens.getRefreshToken()) : false;

        if (!useableAccessToken && !useableRefreshToken) {
          logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
          // httpServletResponse.setStatus(401);
        } else { /* 툴 중 하나라도 사용 가능하다. */
          if (!useableAccessToken) {
            if (!useableRefreshToken) { // refreshToken이 없으면
              logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
              // httpServletResponse.setStatus(401);
            } else { // refreshToken이 있으면
              Authentication authentication = tokenProvider.getAuthentication(tokens.getRefreshToken());
              tokens = tokenProvider.createToken(authentication,response);

              logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);

              /* Token 재발급 */
              response.setHeader(AUTHORIZATION_ACCESS_HEADER, "Bearer " + tokens.getAccessToken());
              // refresh는 기존걸 그대로 쓰도록
            }
          }
        }
      }
    }
    return HandlerInterceptor.super.preHandle(request, response, handler);
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      @Nullable ModelAndView modelAndView) throws Exception {
    /*
     * 클라이언트의 요청을 처리한 뒤에 호출된다.
     * 컨트롤러에서 예외가 발생되면 실행되지 않는다.
     */
    System.out.println("##WHERE JwtInterceptor postHandle");
    HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
      @Nullable Exception ex) throws Exception {
    /*
     * 클라이언트 요청을 마치고 클라이언트에서 응답을 전송한뒤 실행이 된다.
     */
    HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
  }

  /* TokenDto에 토큰 셋팅 */
  private TokenDto resolveToken(HttpServletRequest request) {
    System.out.println("##THIS resolveToken");

    String accessToken = request.getHeader(AUTHORIZATION_ACCESS_HEADER);
    String refreshToken = getRefreshToken(request);

    TokenDto tokenDto = new TokenDto();

    /* ACCESS_TOKEN 가져오기 */
    if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
      tokenDto.setAccessToken(accessToken.substring(7));
    }
    /* REFRESH_TOKEN 가져오기 */
    if (StringUtils.hasText(refreshToken)) {
      tokenDto.setRefreshToken(refreshToken);
    }
    return tokenDto;
  }

  /* 화이트 리스트의 경우 인증 체크X */
  private boolean isExcluded(String requestURI) {
    System.out.println("아니 시붕 :"+requestURI);
    return PatternMatchUtils.simpleMatch(whitelist, requestURI);
  }

  /* cookie에서 refreshToken 가져오기 */
  private String getRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    String refreshToken = "";
    for (Cookie c : cookies) {
      if (c.getName().equals(AUTHORIZATION_REFRESH_HEADER)) {
        refreshToken = c.getValue();
      }
    }
    return refreshToken;
  }
}
