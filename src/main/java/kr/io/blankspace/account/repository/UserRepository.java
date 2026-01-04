package kr.io.blankspace.account.repository;

import kr.io.blankspace.account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserId(String userId);
    boolean existsByUserMail(String userMail);
    Optional<User> findByUserMail(String userMail);
}