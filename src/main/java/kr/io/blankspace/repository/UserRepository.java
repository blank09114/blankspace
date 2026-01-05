package kr.io.blankspace.repository;

import kr.io.blankspace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserMail(String userMail);
    Optional<User> findByUserMail(String userMail);
}