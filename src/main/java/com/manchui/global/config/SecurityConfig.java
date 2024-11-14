package com.manchui.global.config;

import com.manchui.domain.notification.repository.EmitterRepository;
import com.manchui.domain.service.RedisRefreshTokenService;
import com.manchui.global.jwt.CustomLogoutFilter;
import com.manchui.global.jwt.JWTFilter;
import com.manchui.global.jwt.JWTUtil;
import com.manchui.global.jwt.LoginFilter;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final Validator validator;
    private final RedisRefreshTokenService redisRefreshTokenService;
    private final EmitterRepository emitterRepository;
    @Value("${token.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${token.refresh.expiration}")
    private Long refreshTokenExpiration;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors((cors) -> cors.configurationSource(request -> {

                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "https://manchui.vercel.app"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);

                    configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie", "Content-Type"));

                    return configuration;
                }));

        //csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);

        //Form 로그인 방식 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);

        //http basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                "/api/auths/signup",
                                "/api/auths/signin",
                                "/api/auths/check-name",
                                "/api/auths/check-email",
                                "/api/auths/reissue",
                                "/login",

                                // swagger 관련 API 문서 경로
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",

                                // 비회원 조회 경로
                                "/api/gatherings/public/**",
                                "/api/reviews",
                                "/api/reviews/score",

                                // OAuth2 로그인 관련
                                "/login/oauth2/callback/kakao",
                                "/login/oauth2/callback/google",
                                "/login/oauth2/callback/naver"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        //로그인 필터 적용
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, validator
                        , redisRefreshTokenService, accessTokenExpiration, refreshTokenExpiration), UsernamePasswordAuthenticationFilter.class);

        //JWT 필터 적용
        http
                .addFilterAfter(new JWTFilter(jwtUtil, redisRefreshTokenService), LoginFilter.class);


        //커스텀 로그아웃 필터 적용
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, redisRefreshTokenService, emitterRepository), LogoutFilter.class);

        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 예외 처리: 인증 및 접근 거부 처리
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // 인증되지 않은 경우 401 응답
                .accessDeniedHandler((request, response, accessDeniedException) -> { // 권한 부족 시 403 응답
                    log.warn("Access Denied: {}", accessDeniedException.getMessage());
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied");
                })
        );

        return http.build();
    }

}
