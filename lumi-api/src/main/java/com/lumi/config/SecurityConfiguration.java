package com.lumi.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.lumi.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz ->
                // prettier-ignore
                authz
                        .requestMatchers("/api/register").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/authenticate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/authenticate").permitAll()
                        .requestMatchers("/api/activate").permitAll()
                        .requestMatchers("/api/account/reset-password/init").permitAll()
                        .requestMatchers("/api/account/reset-password/finish").permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/subjects", "/api/grades", "/api/subjects/**", "/api/grades/**").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/subjects/**", "/api/grades/**").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/subjects/**", "/api/grades/**").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/subjects/**", "/api/grades/**").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/courses").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/courses/{id}").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/courses/{id}").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/{id}").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers("/api/authorities/**").hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/profile/instructor/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses/{id}/reviews").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses/{id}/overview").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/websocket/**").authenticated()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/management/health").permitAll()
                        .requestMatchers("/management/health/**").permitAll()
                        .requestMatchers("/management/info").permitAll()
                        .requestMatchers("/management/prometheus").permitAll()
                        .requestMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }
}
