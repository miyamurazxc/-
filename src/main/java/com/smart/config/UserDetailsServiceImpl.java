package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

// Реализация интерфейса UserDetailsService.
// Этот класс отвечает за загрузку пользователя при авторизации.
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Метод вызывается Spring Security при попытке входа пользователя.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Ищем пользователя в базе данных по email (username)
        User user = userRepository.getUserByUserName(username);

        // 2. Если пользователя нет — бросаем исключение
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден!");
        }

        // 3. Оборачиваем нашу сущность User в объект,
        // который понимает Spring Security (CustomUserDetails)
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // 4. Возвращаем объект с данными пользователя: роли, пароль, логин
        return customUserDetails;
    }

}
