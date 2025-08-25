package com.vesco.financetracker.config;

import com.vesco.financetracker.filter.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/api/*");
        reg.setOrder(1);
        return reg;
    }
}
