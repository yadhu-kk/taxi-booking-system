package com.edstem.taxibookingandbillingsystem.controller;

import com.edstem.taxibookingandbillingsystem.contract.request.LoginRequest;
import com.edstem.taxibookingandbillingsystem.contract.request.RegisterRequest;
import com.edstem.taxibookingandbillingsystem.contract.request.UpdateAccountRequest;
import com.edstem.taxibookingandbillingsystem.contract.response.LoginResponse;
import com.edstem.taxibookingandbillingsystem.contract.response.SignupResponse;
import com.edstem.taxibookingandbillingsystem.contract.response.UpdateAccountResponse;
import com.edstem.taxibookingandbillingsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public SignupResponse SignUp(@Valid @RequestBody RegisterRequest request) {
        return userService.signUpUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) throws Exception {
        return userService.loginUser(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateAccountResponse> updateBalance(
            @PathVariable Long id, @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(userService.updateAccountBalance(id, request));
    }
}
