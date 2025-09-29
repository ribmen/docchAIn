package com.docchain.user.domain.model;

import com.docchain.user.domain.enums.UserPlans;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class User {

    @Id
    private UUID id;

    private String username;

    private String fullName;

    private String email;

    private String passwordHash;

    private UserPlans userPlan = UserPlans.FREE;

    private LocalDateTime createdAt;

//    @OneToMany
//    private List<UUID> documents;

}
