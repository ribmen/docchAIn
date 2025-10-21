package com.docchain.user.api.controller;

import com.docchain.user.api.model.CreateDocumentRequest;
import com.docchain.user.api.model.DocumentResponseDto;
import com.docchain.user.api.model.UserInput;
import com.docchain.user.api.model.UserResponseDto;
import com.docchain.user.domain.model.User;
import com.docchain.user.infrastructure.http.client.DocumentApiClient;
import com.docchain.user.domain.service.UserDocumentService;
import com.docchain.user.domain.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserDocumentService userDocumentService;
    private final UserRegistrationService userRegistrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserInput user) {
        return userRegistrationService.createUser(user);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<UserResponseDto> createManyUsers(@RequestBody @Valid List<@Valid UserInput> users) {
        return userRegistrationService.createManyUser(users);
    }

    @PutMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable UUID userId, @Valid @RequestBody UserInput userInput) {
        return userRegistrationService.updateUser(userId, userInput);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userRegistrationService.deleteUser(userId);
    }

}