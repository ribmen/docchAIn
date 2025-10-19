package com.docchain.user.api.model;

import com.docchain.user.domain.VO.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInput {

    @NotBlank
    private String fullName;

    @NotBlank
    private String email;

    @NotBlank
    private String passwordHash;

}
