package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration                  // Говорит Spring, что это класс конфигурации
@EnableWebSecurity              // Включаем Spring Security
public class MyConfig extends WebSecurityConfigurerAdapter {

    // Сервис, который загружает пользователя из базы по email
    @Bean
    public UserDetailsService getUserDetailService() {
        return new UserDetailsServiceImpl();
    }

    // Бин шифрования пароля (алгоритм BCrypt)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Настройка провайдера аутентификации (как именно логиниться)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        // Говорим, откуда брать пользователя (наш UserDetailsServiceImpl)
        daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());

        // Как сравнивать пароль (шифровка BCrypt)
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    // Настройка аутентификации: подключаем наш провайдер
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    // Настройка прав доступа и формы логина
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // Админ-раздел: только роль ADMIN
                .antMatchers("/admin/**").hasRole("ADMIN")
                // Личный кабинет /user/**: только роль USER
                .antMatchers("/user/**").hasRole("USER")
                // Все остальные страницы доступны всем
                .antMatchers("/**").permitAll()
                .and()
                // Настройка формы логина
                .formLogin()
                .loginPage("/signin")          // своя страница логина
                .loginProcessingUrl("/dologin")// куда отправляется форма
                .defaultSuccessUrl("/user/index") // куда перенаправлять после успешного входа
                .and()
                // Отключаем CSRF (для простоты в учебном проекте)
                .csrf().disable();
    }

}

