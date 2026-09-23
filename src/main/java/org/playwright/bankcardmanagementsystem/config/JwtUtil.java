package org.playwright.bankcardmanagementsystem.config;



import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;

/*
 Think of a passport office machine. It takes your name (email), stamps an issue date and an expiry date, and laminates it into a tamper-proof ID card. The card has a special invisible security seal that only the bank's machine can create, because only the bank knows the secret ink pattern. Anyone can read the name on the front — but nobody can forge the seal.
 */
/*
Half 1
AuthService.login()
   │  (user verified ✅)
   ▼
jwtUtil.generateToken(user.getEmail())    ← YOU ARE HERE
   │  → creates signed token:  [header].[payload].[signature]
   ▼
LoginResponse(token, email, role)  →  frontend stores it
 */
/*
Half 2
Frontend sends the token (Authorization: Bearer <token>)
   ▼
JwtAuthenticationFilter (bouncer)
   │  jwtUtil.extractUsername(token)   → "who does this badge say?"
   │  jwtUtil.validateToken(token, userDetails)  → "is the seal genuine? still valid?"
   ▼
if OK → request is allowed in
 */
@Component
public class JwtUtil {
    /*
    From application properties
    ${jwt.secret} → Spring finds the property named jwt.secret and injects its value into the secret field.
    ${jwt.expiration} → the token's lifetime in milliseconds (e.g., 86400000 ms = 24 hours), injected into expiration.
    Why keep these in a properties file instead of hardcoding? Because secrets and timeouts are configuration, not code — you can change them per environment (dev vs prod) without recompiling and without the secret sitting in your source code
     */

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;
/*
secret.getBytes() → turns the secret string into raw bytes (crypto works on bytes).
Keys.hmacShaKeyFor(...) → converts those bytes into a proper HMAC-SHA secret key — the "secret ink" for the stamp.
Why private? It's an internal helper — other classes don't need to know how the key is built; they only call the public methods.

Critical security note 🚨: jjwt's HMAC-SHA requires a key of at least 256 bits (32+ characters). If the jwt.secret in application.properties is too short, this line throws a WeakKeyException at runtime. Also — in real production, this secret should come from an environment variable, not a committed file, because whoever knows the secret can forge any token (full account access!).
 */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    /*
    Jwts.builder() — starts building a brand-new token. (The "blank passport".)
.setSubject(username) — the subject: the person this badge belongs to (here, the email). Spring calls it "username" by convention.
.setIssuedAt(new Date()) — stamps the issue time (right now).
.setExpiration(new Date(System.currentTimeMillis() + expiration)) — expiry = now + expiration ms. System.currentTimeMillis() = wall-clock time in ms; add the configured lifetime.
.signWith(getSigningKey()) — the tamper-proof seal: signs it with the secret key. Only the bank's machine can reproduce this signature.
.compact() — compresses it all into the final string — the xxx.yyy.zzz JWT you see in browser dev tools.
     */

    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getSigningKey())
                .compact();
    }
    /*
    extractUsername(token) — reads who the token belongs to (the subject/email).
extractExpiration(token) — reads when it expires.
They both delegate to the generic extractClaim with a different function:

Claims::getSubject — a method reference: "call getSubject() on whatever Claims object you get."
Claims::getExpiration — "call getExpiration()."
     */

    public String extractUsername(String token) {
        return extractClaim(
                token,
                Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(
                token,
                Claims::getExpiration);
    }
/*
<T> — a generic type declaration: "I don't know/care what type you want back — could be a String (username) or a Date (expiration)."
Function<Claims, T> resolver — a function I pass in that takes a Claims object and returns some value T. "Tell me how to pluck a value out of the claims."
extractAllClaims(token) — first, parse + verify the token (below) to get the full Claims map.
resolver.apply(claims) — then run the passed-in function on it: Claims::getSubject returns the username; Claims::getExpiration returns the date.
 */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver) {

        Claims claims =
                extractAllClaims(token);

        return resolver.apply(claims);
    }
/*
Jwts.parserBuilder() — creates a checker machine.
.setSigningKey(getSigningKey()) — gives the checker the same secret ink, so it can verify the seal.
.build() — assembles the checker.
.parseClaimsJws(token) — parses the token AND verifies the signature. If the seal doesn't match (forged or tampered), this throws — the token is rejected right here.
.getBody() — pulls out the Claims (the payload data) for reading.
 */
    private Claims extractAllClaims(
            String token) {



            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

    }
//"Is the badge's expiry date before right now? If yes → expired → true."
    private boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .before(new Date());
    }
/*
What it checks (two things, both must be true):

username.equals(userDetails.getUsername()) — identity match: the email in the token must match the email of the loaded user (from CustomUserDetailsService). A badge stolen with someone else's name on it won't match.
!isTokenExpired(token) — still fresh: not expired.
&& = AND — both conditions must pass. Returns true = "badge is genuine, belongs to this person, still valid — let them in."
 */
    public boolean validateToken(
            String token,
            UserDetails userDetails) {

        String username =
                extractUsername(token);

        return username.equals(
                userDetails.getUsername())
                && !isTokenExpired(token);
    }
}
/*
A JWT is signed, NOT encrypted. The payload (header + claims) is only base64-encoded — anyone can decode it and read the email and timestamps. The signature is what prevents tampering.

So: never put passwords or other secrets inside a JWT. It's fine to put an email — but think of the token like a name tag everyone can read, but only the bank can stamp — not like a sealed envelope.
 */