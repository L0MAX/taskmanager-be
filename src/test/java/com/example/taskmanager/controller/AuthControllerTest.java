package com.example.taskmanager.controller;

import com.example.taskmanager.model.User;
import com.example.taskmanager.payload.request.LoginRequest;
import com.example.taskmanager.payload.request.RegisterRequest;
import com.example.taskmanager.payload.response.ApiResponse;
import com.example.taskmanager.payload.response.AuthResponse;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
    }

    @Test
    void register_NewUser_SavesAndReturnsSuccess() {
       
        RegisterRequest req = new RegisterRequest();
        req.setUsername("alice");
        req.setPassword("secret");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        ApiResponse response = authController.register(req);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("User registered successfully!", response.getMessage());

        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals("alice", saved.getUsername());
        assertEquals("encoded-secret", saved.getPassword());

        verify(passwordEncoder).encode("secret");
    }

    @Test
    void register_ExistingUser_ReturnsFailure() {
       
        RegisterRequest req = new RegisterRequest();
        req.setUsername("bob");
        req.setPassword("pw");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(new User()));

        ApiResponse response = authController.register(req);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("User already exists!", response.getMessage());

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void login_ValidCredentials_ReturnsToken() {
        
        LoginRequest req = new LoginRequest();
        req.setUsername("carol");
        req.setPassword("pw");

        Authentication fakeAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(fakeAuth);

        when(jwtUtil.generateToken("carol")).thenReturn("jwt-token-123");

        AuthResponse resp = authController.login(req);

        assertNotNull(resp);
        assertEquals("jwt-token-123", resp.getToken());

        verify(authenticationManager).authenticate(argThat(token ->
                token instanceof UsernamePasswordAuthenticationToken &&
                "carol".equals(((UsernamePasswordAuthenticationToken) token).getPrincipal())
        ));
        verify(jwtUtil).generateToken("carol");
    }

    @Test
    void login_InvalidCredentials_AuthenticationThrows_exceptionPropagates() {
      
        LoginRequest req = new LoginRequest();
        req.setUsername("dave");
        req.setPassword("bad");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad creds"));

        BadCredentialsException ex = assertThrows(BadCredentialsException.class, () -> authController.login(req));
        assertEquals("Bad creds", ex.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
