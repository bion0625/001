package com.uj.stxtory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
public class Security implements WebMvcConfigurer {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /* @formatter:off */
        http
                .csrf().disable()//todo loginPagecustom을 위해 추가 한 부분, 검색 필요
                .authorizeRequests()
//                .mvcMatchers("/", "/home", "/join").permitAll() // 설정한 리소스의 접근을 인증절차 없이 허용
                .mvcMatchers("/join").permitAll() // 설정한 리소스의 접근을 인증절차 없이 허용
                .anyRequest().authenticated() // 그 외 모든 리소스를 의미하며 인증 필요
                .and()
                .formLogin()
                .permitAll()
                .loginPage("/login") // 기본 로그인 페이지
                .defaultSuccessUrl("/", true) // 로그인 성공 후 이동할 URL
                .and()
                .logout()
                .permitAll()
                // .logoutUrl("/logout") // 로그아웃 URL (기본 값 : /logout)
                // .logoutSuccessUrl("/login?logout") // 로그아웃 성공 URL (기본 값 : "/login?logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // 주소창에 요청해도 포스트로 인식하여 로그아웃
                .deleteCookies("JSESSIONID") // 로그아웃 시 JSESSIONID 제거
                .invalidateHttpSession(true) // 로그아웃 시 세션 종료
                .clearAuthentication(true); // 로그아웃 시 권한 제거

        return http.build();
        /* @formatter:on */
    }

}
