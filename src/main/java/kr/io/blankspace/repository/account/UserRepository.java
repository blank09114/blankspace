package kr.io.blankspace.repository.account;

import kr.io.blankspace.entity.account.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserMail(String userMail);
    Optional<User> findByUserMail(String userMail);
}