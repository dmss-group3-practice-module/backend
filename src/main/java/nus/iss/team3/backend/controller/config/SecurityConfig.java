package nus.iss.team3.backend.controller.config;

import java.util.List;
import nus.iss.team3.backend.service.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Value("#{'${cors.allowed.origins}'.split(',')}")
  private List<String> allowedOrigins;

  private final JwtRequestFilter jwtRequestFilter;

  public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
    this.jwtRequestFilter = jwtRequestFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeRequests(
            authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/authenticate/**")
                    .permitAll()
                    .requestMatchers("/user/check")
                    .permitAll()
                    .requestMatchers("/expiry/**")
                    .authenticated()
                    .requestMatchers("/ingredient/**")
                    .authenticated()
                    .requestMatchers("/notification/**")
                    .authenticated()
                    .requestMatchers("/recipe/**")
                    .authenticated()
                    .requestMatchers("/user/**")
                    .authenticated()
                    .anyRequest()
                    .denyAll())
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    ;
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(
        List.of(
            "Authorization",
            "Cache-Control",
            "Content-Type",
            "X-XSRF-TOKEN",
            "userId",
            "Internal-Service-Call"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
