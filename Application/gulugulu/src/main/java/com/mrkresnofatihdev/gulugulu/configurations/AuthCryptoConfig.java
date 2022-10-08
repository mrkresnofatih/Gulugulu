package com.mrkresnofatihdev.gulugulu.configurations;

import com.mrkresnofatihdev.gulugulu.utilities.HashHelper;
import com.mrkresnofatihdev.gulugulu.utilities.JwtHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthCryptoConfig {
    @Value("${gulugulu.crypto.saltformat}")
    private String saltFormat;

    @Bean
    public HashHelper hashHelper() {
        return new HashHelper(saltFormat);
    }

    @Value("${gulugulu.jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtHelper jwtHelper() {
        return new JwtHelper(jwtSecret);
    }
}
