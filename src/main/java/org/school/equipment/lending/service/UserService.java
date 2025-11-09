package org.school.equipment.lending.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.school.equipment.lending.entity.User;
import org.school.equipment.lending.model.UserSignUpRequestDTO;
import org.school.equipment.lending.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public User signUp(UserSignUpRequestDTO userRequest)   {
        User user = new User();
        user.setRole(userRequest.getRole());
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }
}

