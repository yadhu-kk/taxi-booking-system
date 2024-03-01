package com.edstem.taxibookingandbillingsystem.service;

import com.edstem.taxibookingandbillingsystem.contract.request.LoginRequest;
import com.edstem.taxibookingandbillingsystem.contract.request.RegisterRequest;
import com.edstem.taxibookingandbillingsystem.contract.request.UpdateAccountRequest;
import com.edstem.taxibookingandbillingsystem.contract.response.LoginResponse;
import com.edstem.taxibookingandbillingsystem.contract.response.SignupResponse;
import com.edstem.taxibookingandbillingsystem.contract.response.UpdateAccountResponse;
import com.edstem.taxibookingandbillingsystem.exception.EntityAlreadyExistsException;
import com.edstem.taxibookingandbillingsystem.exception.InvalidLoginException;
import com.edstem.taxibookingandbillingsystem.model.User;
import com.edstem.taxibookingandbillingsystem.repository.UserRepository;
import com.edstem.taxibookingandbillingsystem.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public SignupResponse signUpUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityAlreadyExistsException(request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountBalance(0D)
                .build();
        userRepository.save(user);
        return modelMapper.map(user, SignupResponse.class);
    }

    public LoginResponse loginUser(LoginRequest loginRequest) throws Exception {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        if (!userRepository.existsByEmail(email)) {
            throw new EntityNotFoundException("Invalid login");
        }
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtService.generateToken(user);
        }
        throw new InvalidLoginException();
    }

    public UpdateAccountResponse updateAccountBalance(Long id, UpdateAccountRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("id not found"));
        user = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .accountBalance(user.getAccountBalance() + request.getAccountBalance())
                .build();
        User updateUser = userRepository.save(user);
        return modelMapper.map(user, UpdateAccountResponse.class);
    }
}