package org.playwright.bankcardmanagementsystem.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
// It defines what data the client must send to log in, and what rules that data must obey — enforced at the front door, before any business logic runs.
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
/*
Constructor chaining is the process of calling one constructor from another constructor using this() (same class) or super() (parent class) to reuse initialization code and avoid duplication.
 */