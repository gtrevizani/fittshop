package com.fittshop.service;

import com.fittshop.config.JwtTokenProvider;
import com.fittshop.domain.enums.RoleType;
import com.fittshop.domain.model.Customer;
import com.fittshop.domain.model.LoyaltyAccount;
import com.fittshop.repository.CustomerRepository;
import com.fittshop.repository.LoyaltyAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final CustomerRepository customerRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(CustomerRepository customerRepository,
                       LoyaltyAccountRepository loyaltyAccountRepository,
                       JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.loyaltyAccountRepository = loyaltyAccountRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> authenticate(String email, String rawPassword) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        if (optionalCustomer.isEmpty()) {
            throw new IllegalArgumentException("Credenciais inválidas: e-mail não encontrado.");
        }

        Customer customer = optionalCustomer.get();

        // Checa compatibilidade BCrypt ou fallback de dev
        boolean matches = passwordEncoder.matches(rawPassword, customer.getPassword())
                || customer.getPassword().equals(rawPassword)
                || customer.getPassword().equals("{noop}" + rawPassword);

        if (!matches) {
            throw new IllegalArgumentException("Credenciais inválidas: senha incorreta.");
        }

        String token = jwtTokenProvider.generateToken(
                customer.getEmail(),
                customer.getRole().name(),
                customer.getName(),
                customer.getId()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", customer.getRole().name());
        response.put("name", customer.getName());
        response.put("email", customer.getEmail());
        response.put("userId", customer.getId());

        return response;
    }

    public Map<String, Object> register(String name, String email, String password, String phone, String cpf) {
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado na plataforma.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        Customer customer = new Customer(name, email, encodedPassword, cpf, phone, RoleType.ROLE_CUSTOMER);
        Customer saved = customerRepository.save(customer);

        // Inicializa conta de fidelidade com 50 FittCoins de boas-vindas
        loyaltyAccountRepository.save(new LoyaltyAccount(saved.getId(), 50, "BRONZE"));

        return authenticate(email, password);
    }
}
