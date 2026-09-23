package org.playwright.bankcardmanagementsystem.config;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.playwright.bankcardmanagementsystem.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/*

Import	Plain English
FilterChain	The relay race baton — passing the request along to the next filter in the chain.
ServletRequest/ServletException	Java/HTTP fundamentals — the request, response, and exception types of the servlet world.
RequiredArgsConstructor	Lombok constructor injection (your 5th time seeing it! by now it should be automatic 😄).
CustomUserDetailsService	The ID-verification desk you read. Used to load the user.
UsernamePasswordAuthenticationToken	The security "this person is authenticated" envelope — same class AuthService used, but differently (filled with the loaded user, not a login attempt).
SecurityContextHolder	The visitor log — the thread-local place Spring Security stores "who is the current user."
UserDetails	The standard user summary from CustomUserDetailsService.
WebAuthenticationDetailsSource	Helper that attaches extra request details (like IP) to the auth object.
Component	"Spring, manage me as a bean."
OncePerRequestFilter	The base class: guarantees our filter runs exactly once per request (even with forwarding/async it won't double-run).
 */
@Component
@RequiredArgsConstructor
//OncePerRequestFilter is a Spring filter that guarantees its logic runs only once for a single HTTP request. It is commonly used in Spring Security for JWT authentication, logging, and request validation to avoid executing the same logic multiple times during request forwarding or dispatching.
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    //fulfilling the contract from OncePerRequestFilter
    @Override
    /*
    HttpServletRequest request — everything about the incoming HTTP request (headers, path, body, method).
HttpServletResponse response — what we'll send back (though this filter mostly passes it on untouched).
FilterChain filterChain — the baton: lets us hand the request to the next filter when we're done.
     */
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
//Looks at which URL is being requested. If it starts with /api/auth (like /api/auth/login), the filter skips all badge-checking and passes the request straight along.If removed (or if login requests aren't exempted): nobody could ever log in, because the login request itself would be rejected as "no badge."
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
//Security guard asks for the ID
        String authHeader =
                request.getHeader("Authorization");

        String token = null;
        String username = null;
        // Security guard checks if the visitor has provided an Authorization header
        // and whether it is a Bearer token (company-approved ID card)
        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {
        // Remove the "Bearer " prefix to get the actual JWT token.
        // Example:
        // "Bearer abc.xyz.123" -> "abc.xyz.123"
            token = authHeader.substring(7);
            // Read the username stored inside the JWT token,

            // similar to reading the employee's name from their ID card.
            username = jwtUtil.extractUsername(token);
        }
//"If we read a name off a badge AND the visitor log doesn't already say this person is cleared, then do the real check."

        if (username != null &&
                SecurityContextHolder.getContext()
                        .getAuthentication() == null) {
//The guard takes the name from the badge and calls the ID desk (CustomUserDetailsService) to pull the official record — the real user with their encoded password and authorities from the database.
            UserDetails userDetails =
                    customUserDetailsService
                            .loadUserByUsername(username);
//The token's username matches the loaded user's username.
//The token isn't expired. (And signature verification already happened inside extractAllClaims when we read the token.)
            if (jwtUtil.validateToken(token, userDetails)) {
// Create an authenticated security token for the verified user.

// It contains user details and roles/permissions.

// Password is set to null because authentication was already done using JWT.
                UsernamePasswordAuthenticationToken authToken =
                        //"This user has been successfully authenticated."
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
// The guard stamps your visitor badge and writes your name in the visitor log. Now every employee you meet inside knows you're cleared and what floors you may access.
                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }
//Why here: The filter's job is just "if there's a valid token, mark the user as authenticated." It doesn't make access decisions — the authorization layer does. Clean separation of duties: the filter fills the visitor log; the rules decide who gets into each room.
        filterChain.doFilter(request, response);
    }
}
/*
1. Employee arrives at the gate.
2. Guard asks for ID card (Authorization Header).
3. Guard checks if ID card exists.
4. Guard checks if it starts with "Bearer ".
5. Guard removes "Bearer " and extracts JWT token.
6. Guard opens the JWT and reads the username.
7. Guard checks company database for that username.
8. Guard validates that the JWT is genuine and not expired.
9. Guard creates an authenticated security pass.
10. Guard updates the security system (SecurityContext).
11. Employee is allowed to access protected resources.
 */
/*
@Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    String path = request.getServletPath();

    if (path.startsWith("/api/auth")) {
        filterChain.doFilter(request, response);
        return;
    }

    // existing JWT logic here
}
 */