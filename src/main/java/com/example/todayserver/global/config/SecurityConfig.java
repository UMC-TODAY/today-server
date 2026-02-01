package com.example.todayserver.global.config;

import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.member.service.MemberService;
import com.example.todayserver.global.common.jwt.JwtAuthFilter;
import com.example.todayserver.global.common.jwt.JwtUtil;
import com.example.todayserver.global.oauth.OAuth2SuccessHandler;
import com.example.todayserver.global.oauth.OAuth2UserCustomService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    private final String[] allowUris = {
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/api/members/password/reset",
            "/api/auth/**",
            "/api/oauth2/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2UserCustomService oAuth2UserCustomService, OAuth2SuccessHandler oAuth2SuccessHandler) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(allowUris).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("""
                            {
                              "code": "LOGIN_REQUIRED",
                              "message": "로그인이 필요한 서비스입니다."
                            }
                            """);
                }))
                // 폼로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserCustomService))
                        .successHandler(oAuth2SuccessHandler));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil, memberRepository);
    }

}
