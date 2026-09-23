package org.playwright.bankcardmanagementsystem.service;



import lombok.RequiredArgsConstructor;
import org.playwright.bankcardmanagementsystem.entity.User;
import org.playwright.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
/*
Think of a bank's ID-verification desk worker. When the security officer (Spring's AuthenticationManager) receives a login attempt, it doesn't know who you are — so it calls this worker and asks: "Do you have a file on this person? What is their officially recorded password and their hierarchy/access level?" This worker pulls your file (from the repository), and hands back a tidy summary card to the security officer.
 */
@Service
@RequiredArgsConstructor
//"I agree to the contract that a user-loader must follow." The contract requires one method: loadUserByUsername(String).
public class CustomUserDetailsService
        implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
//tells Java "this method is fulfilling a promise made by an interface/parent class." Here, it's satisfying the UserDetailsService interface.
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                /*
                the authorities list. The ROLE_ prefix is a Spring Security convention that turns our role (e.g., ADMIN) into a proper authority string (ROLE_ADMIN), which later determines what the user is allowed to do.
💡 Connection worth remembering: This is where user.getRole() from our login flow actually matters — it becomes the authority that Spring Security will check when the frontend calls protected endpoints.
                 */
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                )
        );
    }
}
