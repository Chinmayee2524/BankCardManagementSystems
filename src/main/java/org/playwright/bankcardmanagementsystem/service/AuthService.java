package org.playwright.bankcardmanagementsystem.service;



import lombok.RequiredArgsConstructor;
import org.playwright.bankcardmanagementsystem.config.JwtUtil;
import org.playwright.bankcardmanagementsystem.dto.LoginRequest;
import org.playwright.bankcardmanagementsystem.dto.LoginResponse;
import org.playwright.bankcardmanagementsystem.entity.User;
import org.playwright.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
/*
AuthService handles authentication.
It uses AuthenticationManager to validate email/password through Spring Security, then it loads the matching User from the repository, and on success issues a JWT via JwtUtil — returning a LoginResponse with the token, email, and role.
The catch block intentionally swaps all failures to a generic BadCredentialsException so we don't leak information."
 */
@Service //Tells Spring: "This class is a business logic manager. Please create one instance of it, keep it in the container, and make it available for injection into controllers."
@RequiredArgsConstructor //
public class AuthService {

    private final AuthenticationManager authenticationManager; //the security inspector from Spring Security.
    private final UserRepository userRepository; //the filing clerk for the users table.
    private final JwtUtil jwtUtil; //the badge stamper that creates tokens.

    public LoginResponse login(LoginRequest request) {
        //prove the identity. If anything fails, say 'Sorry, wrong username or password.
        try {
            //Asks Spring Security to verify the credentials. It will call CustomUserDetailsService behind the scenes
            authenticationManager.authenticate(
                    //Packs email + password into Spring's standard "I want to prove I am this person" envelope, so Spring Security knows what to check.
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (Exception ex) {
            //If no user with that email exists (returned as an empty Optional), it throws BadCredentialsException — again with the friendly, generic message.
            throw new BadCredentialsException(
                    "Invalid username or password");
        }
        // Asks the repository to look up the full user row by email.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid username or password"));
        //Uses JwtUtil to turn the user's email into a JWT — a signed, time-limited visitor badge comparing claims: "this is the email X, token made at Y, valid until Z," stamped with a secret so it can't be forged.
        String token =
                jwtUtil.generateToken(user.getEmail());
/*
Packages up the result into a LoginResponse.

token — the visitor badge.
user.getEmail() — who logged in.
user.getRole().name() — the user's role (e.g., ADMIN, CUSTOMER) as a String, so the frontend knows the type of menu to show.
 */
        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }
}
