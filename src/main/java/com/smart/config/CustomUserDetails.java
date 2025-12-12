package com.smart.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smart.entities.User;

// Класс-адаптер: превращает наш User в объект, который понимает Spring Security
public class CustomUserDetails implements UserDetails {

    private User user; // Наш пользователь из базы данных

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Возвращаем роль пользователя (например ROLE_USER)
        SimpleGrantedAuthority simpleGrantedAuthority =
                new SimpleGrantedAuthority(user.getRole());

        return List.of(simpleGrantedAuthority);
    }

    @Override
    public String getPassword() {
        // Возвращаем зашифрованный пароль из БД
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Spring Security использует email как username
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // true — аккаунт никогда не истекает
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // true — аккаунт не заблокирован
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // true — пароль не просрочен
        return true;
    }

    @Override
    public boolean isEnabled() {
        // true — пользователь активен, может входить
        return true;
    }
}

