package com.fittshop.service;

import com.fittshop.domain.enums.RoleType;
import com.fittshop.domain.model.Customer;
import com.fittshop.repository.CustomerRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;

    @Value("${fittshop.security.jwt.secret}")
    private String jwtSecret;

    @Value("${fittshop.security.jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    public AuthService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Map<String, Object> authenticate(String email, String password) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        if (optionalCustomer.isEmpty()) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        Customer customer = optionalCustomer.get();

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .subject(customer.getEmail())
                .claim("role", customer.getRole().name())
                .claim("name", customer.getName())
                .claim("userId", customer.getId())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", customer.getRole().name());
        response.put("name", customer.getName());
        response.put("email", customer.getEmail());
        response.put("userId", customer.getId());

        return response;
    }
}
