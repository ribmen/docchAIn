package com.docchain.user.domain.service;

import com.docchain.user.api.model.UserInput;
import com.docchain.user.domain.VO.Email;
import com.docchain.user.domain.model.User;
import com.docchain.user.domain.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;

    public User createUser(@Valid UserInput userInput) {
        User user = User.brandNew(userInput.getFullName(), userInput.getEmail(), userInput.getPasswordHash());
        return userRepository.saveAndFlush(user);
    }

    public User updateUser(UUID userId, @Valid UserInput userInput) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        user.setFullName(userInput.getFullName());
        user.setEmail(new Email(userInput.getEmail()));
        return userRepository.saveAndFlush(user);
    }

}
