package com.clouds.cloud_sprint.security;

import com.clouds.cloud_sprint.model.Users;
import com.clouds.cloud_sprint.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Попытка загрузки пользователя: {}", username);

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Пользователь {} не найден", username);
                    return new UsernameNotFoundException("Пользователь не найден");
                });

        logger.info("Пользователь {} успешно загружен", username);

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER") // Добавляем роль
                .build();
    }
}
