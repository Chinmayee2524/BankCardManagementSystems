package org.playwright.bankcardmanagementsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Your badge machine (JwtUtil) and your ID-verification desk (CustomUserDetailsService) are built — but this file is the switchboard that actually plugs them in and lays out the access policy for the whole app.
/*

Import	What it is (plain English)
Configuration	"This class holds setup instructions."
EnableMethodSecurity	"Allow method-level @PreAuthorize role checks."
Bean	"Each method with this returns a Spring-managed object."
AuthenticationManager	Spring's identity-checker (met in AuthService).
AuthenticationConfiguration	A provider that gives us the auth manager.
HttpSecurity	The object we use to write the security rules.
SecurityFilterChain	The list of filters every request passes through.
SessionCreationPolicy	Decides "should I track server sessions or not?"
BCryptPasswordEncoder	The password hasher (how we store passwords safely).
PasswordEncoder	The interface for "an object that hashes passwords."
UsernamePasswordAuthenticationFilter	Spring's built-in login filter (we insert before it).
Customizer	Spring's "use sensible defaults" helper.
RequiredArgsConstructor	Lombok constructor injection (met 4 times now!).

 */
//"This is a setup class. Spring, call my @Bean methods to create objects it can manage."
@Configuration
/*
"Turn on method-level security: now any controller method can be annotated with things like @PreAuthorize("hasRole('ADMIN')") for extra protection." (For completeness — this project's controllers are not using @PreAuthorize; the path-based rules below do the work. Still good to know what the flag enables.)
 */
@EnableMethodSecurity

@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    //"whatever this method returns (a SecurityFilterChain) is a Spring-managed asset."
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
/*
What is CSRF? Cross-Site Request Forgery — an attack where a malicious site tricks a logged-in user's browser into sending requests to your app as if the user authorized them. Websites using cookies use a CSRF token to block this.

Why disable it here? Because this is a stateless JWT API — it doesn't use cookies at all; the frontend sends the Authorization: Bearer <token> header instead. Without cookies, CSRF protection is unnecessary (an attacker can't ride a cookie the app never sets). Disabling it avoids needless complications.
 */
        http
                .csrf(csrf -> csrf.disable())
//Enables CORS with default settings. Why? Remember @CrossOrigin on the controller from earlier? This is a global CORS layer on top — it handles cross-origin browser requests (your React frontend at port 3000 talking to this server at 8080).
                .cors(Customizer.withDefaults())
/*
What it does: Tells Spring Security: "do NOT create server-side sessions."

Why this matters — and what "stateless" really means: In old-school auth, after login the server remembers you (a session record). In this JWT design, the server remembers nothing — the badge itself carries the proof. "Stateless" = "we don't keep a state list of logged-in users." This is a deliberate architectural decision that pairs perfectly with the stateless JWT design you saw in JwtUtil.

Leaving the badge as the source of truth means: no server memory, effortless horizontal scaling, self-able to load-balance across machines — but it also means (as you noted at logout) you can't forcibly revoke a token server-side.
 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs"
                        )
                        .permitAll()

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/customer/**")
                        .hasRole("CUSTOMER")

                        .requestMatchers("/api/support/**")
                        .hasRole("SUPPORT_AGENT")
//Any request not matched above must come from an authenticated user (anyone holding a valid badge — any role). This is the default deny-public-access safety net — nothing unauthenticated slips through except the explicitly-permitted public paths.
                        .anyRequest()
                        .authenticated()
                )
/*
Slots our JwtAuthenticationFilter (the badge-checker) into the chain, before Spring's built-in UsernamePasswordAuthenticationFilter.

Why before it? Because we want our JWT bouncer to run first — it intercepts the badge at the front gate, before any other logic runs. (Spring's own username/password filter is for form/cookie logins, which this app isn't using; our JWT filter takes over that job.)
 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
//Creates and registers a BCryptPasswordEncoder — the password-hashing machine. Why BCrypt? BCrypt is a slow, salted, one-way hash — deliberately expensive so that even with the database leak, brute-forcing hashed passwords takes forever. It's the industry-standard way to store passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//Provides the AuthenticationManager bean by asking Spring's AuthenticationConfiguration for its standard one.
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }
}