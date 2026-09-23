package org.playwright.bankcardmanagementsystem.controller;



import lombok.RequiredArgsConstructor;
import org.playwright.bankcardmanagementsystem.dto.DashboardResponse;
import org.playwright.bankcardmanagementsystem.entity.User;
import org.playwright.bankcardmanagementsystem.repository.UserRepository;
import org.playwright.bankcardmanagementsystem.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping
    public DashboardResponse getDashboard(
            Authentication authentication)
            throws InterruptedException {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        return dashboardService.getDashboard(
                user.getId());
    }
}