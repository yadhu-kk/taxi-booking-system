package com.edstem.taxibookingandbillingsystem.service;

import com.edstem.taxibookingandbillingsystem.contract.request.LoginRequest;
import com.edstem.taxibookingandbillingsystem.contract.request.RegisterRequest;
import com.edstem.taxibookingandbillingsystem.contract.request.UpdateAccountRequest;
import com.edstem.taxibookingandbillingsystem.contract.response.LoginResponse;
import com.edstem.taxibookingandbillingsystem.contract.response.SignupResponse;
import com.edstem.taxibookingandbillingsystem.contract.response.UpdateAccountResponse;
import com.edstem.taxibookingandbillingsystem.exception.EntityAlreadyExistsException;
import com.edstem.taxibookingandbillingsystem.model.User;
import com.edstem.taxibookingandbillingsystem.repository.UserRepository;
import com.edstem.taxibookingandbillingsystem.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testSignupUser() {
        RegisterRequest request = new RegisterRequest("yadhu", "yadhu@gmail.com", "password1");
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        SignupResponse expectedResponse = new SignupResponse(1L, "yadhu", "yadhu@gmail.com");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, SignupResponse.class)).thenReturn(expectedResponse);

    }

    @Test
    public void login_withValidCredentials_shouldReturnAuthToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("yadhu@gmail.com");
        request.setPassword("password");
        User savedUser = new User();
        savedUser.setName("yadhu");
        savedUser.setEmail("yadhu@gmail.com");
        savedUser.setPassword("hashedPassword");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(savedUser);
        when(passwordEncoder.matches(request.getPassword(), savedUser.getPassword())).thenReturn(true);
        LoginResponse expectedToken = new LoginResponse();
        expectedToken.setName("yadhu");
        expectedToken.setToken("validToken");
        when(jwtService.generateToken(savedUser)).thenReturn(expectedToken);
        LoginResponse actualToken = userService.loginUser(request);
        assertEquals(expectedToken, actualToken);
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).matches(request.getPassword(), savedUser.getPassword());
        verify(jwtService, times(1)).generateToken(savedUser);
    }

    @Test
    public void testEntityAlreadyExistsException() {
        String entity = "User already exist";
        EntityAlreadyExistsException exception =
                assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> {
                            throw new EntityAlreadyExistsException(entity);
                        });

        assertEquals(entity, exception.getEntity());
        assertEquals(0L, exception.getId());
        assertEquals(entity, exception.getMessage());
    }

    @Test
    void testUpdateAccountBalance() {
        User user = new User();
        user.setAccountBalance(10.0d);
        user.setEmail("yadhu@gmail.com");
        user.setId(1L);
        user.setName("yadhu");
        user.setPassword("password1");
        Optional<User> ofResult = Optional.of(user);
        User user2 = new User();
        user2.setAccountBalance(10.0d);
        user2.setEmail("yadhu@gmail.com");
        user2.setId(1L);
        user2.setName("yadhu");
        user2.setPassword("password1");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(modelMapper.map(Mockito.<Object>any(), Mockito.<Class<UpdateAccountResponse>>any()))
                .thenThrow(new EntityAlreadyExistsException("Entity"));
        assertThrows(EntityAlreadyExistsException.class,
                () -> userService.updateAccountBalance(1L, new UpdateAccountRequest(10.0d)));
        verify(modelMapper).map(Mockito.<Object>any(), Mockito.<Class<UpdateAccountResponse>>any());
        verify(userRepository).findById(Mockito.<Long>any());
        verify(userRepository).save(Mockito.<User>any());
    }
}
