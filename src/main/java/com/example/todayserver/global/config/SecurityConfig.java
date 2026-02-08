package com.example.todayserver.global.config;

import com.example.todayserver.domain.member.repository.MemberRepository;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            "/api/v1/members/password/reset",
            "/api/v1/auth/**",
            "/api/oauth2/**",
            "/login/oauth2/**",
            "/login/oauth2/code/**",
            "/oauth2/**",
            "/login/**",
            "/error",
            "/favicon.ico"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2UserCustomService oAuth2UserCustomService, OAuth2SuccessHandler oAuth2SuccessHandler) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(allowUris).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
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
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserCustomService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            // 1. 서버 콘솔에 로그 출력
                            System.err.println("============== 로그인 실패 ==============");
                            System.err.println("원인: " + exception.getMessage());

                            // 2. 프론트엔드에 JSON으로 원인 전달
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");

                            String jsonResponse = String.format(
                                    "{\"code\": \"LOGIN_FAILED\", \"message\": \"%s\"}",
                                    exception.getMessage() // 여기에 진짜 이유가 담깁니다!
                            );
                            response.getWriter().write(jsonResponse);
                        })
                );

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 주소 허용
        configuration.addAllowedOrigin("http://localhost:5173");

        // 허용할 헤더와 메서드
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        // 중요: "쿠키(Credential)를 서로 주고받을 수 있게 허락함"
        configuration.setAllowCredentials(true);

        // 중요: "헤더에 Authorization(JWT) 같은 게 있어도 읽을 수 있게 함"
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Set-Cookie");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
