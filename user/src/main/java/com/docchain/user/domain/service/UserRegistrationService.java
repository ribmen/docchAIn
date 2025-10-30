package com.docchain.user.domain.service;

import com.docchain.user.api.model.UserInput;
import com.docchain.user.api.model.UserResponseDto;
import com.docchain.user.domain.VO.Email;
import com.docchain.user.domain.exception.BusinessException;
import com.docchain.user.domain.exception.UserNotFoundException;
import com.docchain.user.domain.model.User;
import com.docchain.user.domain.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto createUser(@Valid UserInput userInput) {
        Email email = new Email(userInput.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(String.format("O e-mail '%s' já está em uso.", email));
        }

        User user = User.brandNew(userInput.getFullName(), userInput.getEmail(), userInput.getPasswordHash());
        userRepository.saveAndFlush(user);

        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail().toString());
        response.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return response;
    }

    @Transactional
    public List<UserResponseDto> createManyUser(List<UserInput> inputs) {

        List<User> users = inputs
                .stream()
                .map(input -> User.brandNew(
                                input.getFullName(),
                                input.getEmail(),
                                input.getPasswordHash())
                        )
                .toList();
        userRepository.saveAll(users);

        return users.stream()
                .map(UserResponseDto::from)
                .toList();
    }

    public UserResponseDto updateUser(UUID userId, @Valid UserInput userInput) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
        user.setFullName(userInput.getFullName());
        user.setEmail(new Email(userInput.getEmail()));
        User savedUser = userRepository.saveAndFlush(user);

        return UserResponseDto.from(savedUser);
    }

    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId.toString());
        }
        userRepository.deleteById(userId);
        // fazer implementação para apagar documentos associados ao usuário
    }
}
