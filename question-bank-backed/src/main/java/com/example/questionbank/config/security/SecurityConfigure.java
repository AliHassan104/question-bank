package com.example.questionbank.config.security;

import com.example.questionbank.service.impl.MyUserDetailServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigure extends WebSecurityConfigurerAdapter {

    // FIXED: Updated public URLs to match your actual endpoints
    public static final String[] PUBLIC_URLS = {
            "/api/login",             // ✅ Correct endpoint from LoginController
            "/api/signup",            // ✅ Correct endpoint from LoginController
            "/api/auth/register",     // Add if you have registration
            "/v3/api-docs/**",
            "/v2/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/configuration/ui",
            "/configuration/security",
            "/documentation/**",
            "/actuator/health"        // Health check endpoint
    };

    @Autowired
    private MyUserDetailServiceImplementation myUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                // Enable CORS with our secure configuration
//                .cors().configurationSource(corsConfigurationSource)
//                .and()
//
//                // Disable CSRF for stateless API
//                .csrf().disable()
//
//                // Configure authorization
//                .authorizeRequests()
//                .antMatchers(PUBLIC_URLS).permitAll()  // ✅ Allow these without authentication
//                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // ✅ Allow preflight requests
//                .antMatchers(HttpMethod.GET, "/api/*/search").hasAnyRole("USER", "ADMIN")
//                .antMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")
//                .antMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "TEACHER")
//                .antMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "TEACHER")
//                .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
//                .and()
//
//                // Configure exception handling
//                .exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .and()
//
//                // Stateless session management
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//
//                // Security headers
//                .headers()
//                .frameOptions().deny()
//                .contentTypeOptions().and()
//                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
//                        .maxAgeInSeconds(31536000)
//                        .includeSubDomains(true))
//                .and();
//
//        // Add JWT filter
//        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//    }

    // Update your SecurityConfigure.java configure method with this fix:

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // Enable CORS with our secure configuration
                .cors().configurationSource(corsConfigurationSource)
                .and()

                // Disable CSRF for stateless API
                .csrf().disable()

                // Configure authorization
                .authorizeRequests()
                .antMatchers(PUBLIC_URLS).permitAll()  // Allow these without authentication
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Allow preflight requests

                // FIX: Update these lines to match your role structure
                // Your security expects hasRole('USER') which looks for 'ROLE_USER'
                // But your database has 'ADMIN', 'TEACHER', 'STUDENT'

                // Option 1: Use hasAuthority instead of hasRole (recommended quick fix)
                .antMatchers(HttpMethod.GET, "/api/*/search").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")
                .antMatchers(HttpMethod.GET, "/api/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")
                .antMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER")
                .antMatchers(HttpMethod.PUT, "/api/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER")
                .antMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("ROLE_ADMIN")

                // Option 2: Use hasRole but update to match your actual role names
                // .antMatchers(HttpMethod.GET, "/api/*/search").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                // .antMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                // .antMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN", "TEACHER")
                // .antMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN", "TEACHER")
                // .antMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                .anyRequest().authenticated()
                .and()

                // Configure exception handling
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()

                // Stateless session management
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // Security headers
                .headers()
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                        .maxAgeInSeconds(31536000)
                        .includeSubDomains(true))
                .and();

        // Add JWT filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // Use strength 12 for better security (default is 10)
        return new BCryptPasswordEncoder(12);
    }
}