package com.docchain.user.domain.repository;

import com.docchain.user.domain.VO.Email;
import com.docchain.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(Email email);

    Optional<User> findByEmail(Email email);
}
