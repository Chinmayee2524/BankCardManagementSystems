package org.playwright.bankcardmanagementsystem.controller;



import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.playwright.bankcardmanagementsystem.dto.LoginRequest;
import org.playwright.bankcardmanagementsystem.dto.LoginResponse;
import org.playwright.bankcardmanagementsystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// It's the entry point for authentication HTTP requests. It takes JSON from the outside world, hands it to the business brain, and returns a proper HTTP response (with status codes) to the caller.
/*
@Controller → "This class knows how to handle web requests."
@ResponseBody (built in) → "Whatever a method returns, write it directly into the HTTP response body as JSON, don't look for a view/template."
Analogy: this is the receptionist's uniform/name tag that tells Spring: "put me at the front desk."
a plain @Controller expects to return a view name (an HTML template). @RestController skips all that and just returns data (JSON). Since this project is an API (data server), @RestController is correct.
 */
@RestController
@RequestMapping("/api/auth") //"Every endpoint in this class will be reached under the base address /api/auth."
@RequiredArgsConstructor // it will create a constructor that takes AuthService and hands it into this class. (Recap: final fields tell Lombok "these are required.")
/*
Browsers have a security rule: a page served from one address (e.g., http://localhost:3000 — your React app) is blocked from calling APIs on a different address (e.g., http://localhost:8080 — this Spring app). That rule is called Same-Origin Policy — a browser firewall.

What @CrossOrigin does: It adds special headers to responses — *"Access-Control-Allow-Origin: " — telling the browser: "It's fine, I'm open to other origins." This is how the frontend and backend can talk during development.

Without it: the React app's fetch would fail mysteriously in the browser console with a CORS error (even though the backend code is fine).
 */
@CrossOrigin
public class AuthController {
//Why final? It guarantees the field is set once when the object is created and never replaced — safer and it's exactly what Lombok's @RequiredArgsConstructor looks for.
    private final AuthService authService;
/*
ResponseEntity<LoginResponse> — "This method will answer with a full HTTP response whose body is a LoginResponse (token + email + role)."
@Valid — "Before anything else, run the validation rules on the DTO (we'll see them in LoginRequest — e.g., email can't be blank, is properly formatted)." The intent: block garbage/malicious input at the door, never let it reach the business logic or database.
@RequestBody — "Take the JSON text in the request body and deserialize (convert) it into a LoginRequest Java object." This is the magic of Jackson (the JSON library Spring uses) — no manual parsing.
LoginRequest request — the converted object, now usable as Java.
 */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
//wraps the result with HTTP status 200 OK.
        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {

        return ResponseEntity.ok(
                "Logged out successfully");
    }
}
// after logout, the frontend is expected to delete the stored token from its own memory (localStorage/sessionStorage). The token stays technically valid until it expires — a real-world security consideration worth remembering (and a great interview talking point!).