package me.abdul.axi_api.configs;

import me.abdul.axi_api.middleware.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Value("${cors.origin}")
    private String corsOrigin;

    private final JwtAuthFilter jwtAuthFilter;


    public SecurityConfiguration(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorizeRequests ->
                    authorizeRequests
                            .requestMatchers("/auth/**").permitAll()
                            .requestMatchers("team/create").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("team/{id}/forms").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("team/{id}/form/**").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("team/forms").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("question/**").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("/team/create").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("/user/{id}/roles").hasRole("ADMIN")
                            .requestMatchers("/team/{id}/edit").hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers("/team/{id}/feedback/all").hasAnyRole("MANAGER")
                            .requestMatchers("/team/{id}/feedback/all/{receiverId}").hasAnyRole("MANAGER")
                            .requestMatchers(HttpMethod.DELETE,"team/{id}").hasAnyRole("ADMIN")
                            .requestMatchers("/team/{teamId}/report").permitAll()
                            .requestMatchers("/team/{teamId}/report/**").permitAll()
                            .anyRequest().authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}