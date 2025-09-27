package com.example.taskmanager.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() throws NoSuchMethodException {
        
        class Dummy {
            private String name;
            @SuppressWarnings("unused")
            public String getName() { return name; }
            @SuppressWarnings("unused")
            public void setName(String name) { this.name = name; }
        }
        Dummy dummy = new Dummy();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dummy, "dummy");
        bindingResult.rejectValue("name", "NotBlank", "must not be blank");

        Method method = this.getClass().getMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<Map<String, Object>> responseEntity = handler.handleValidationException(ex);
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertTrue(((Map<?, ?>) body.get("errors")).containsKey("name"));
    }

    public void dummyMethod(String param) {}

    @Test
    void handleBadCredentials_ShouldReturnUnauthorized() {
        BadCredentialsException ex = new BadCredentialsException("bad credentials");
        ResponseEntity<Map<String, Object>> responseEntity = handler.handleBadCredentials(ex);
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(401, body.get("status"));
        assertEquals("Invalid username or password", body.get("error"));
    }

    @Test
    void handleJwtException_ExpiredJwt_ShouldReturnUnauthorized() {
        ExpiredJwtException ex = new ExpiredJwtException(null, null, "token expired");
        ResponseEntity<Map<String, Object>> responseEntity = handler.handleJwtException(ex);
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(401, body.get("status"));
        assertEquals("Invalid or expired token", body.get("error"));
    }

    @Test
    void handleJwtException_MalformedJwt_ShouldReturnUnauthorized() {
        MalformedJwtException ex = new MalformedJwtException("malformed token");
        ResponseEntity<Map<String, Object>> responseEntity = handler.handleJwtException(ex);
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(401, body.get("status"));
        assertEquals("Invalid or expired token", body.get("error"));
    }

    @Test
    void handleNotFound_ShouldReturnNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Task not found");
        ResponseEntity<Map<String, Object>> responseEntity = handler.handleNotFound(ex);
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(404, body.get("status"));
        assertEquals("Task not found", body.get("error"));
    }

    @Test
    void handleRuntime_ShouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("unexpected error");
        ResponseEntity<Map<String, Object>> responseEntity = handler.handleRuntime(ex);
        Map<String, Object> body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Something went wrong. Please try again later.", body.get("error"));
    }
}
