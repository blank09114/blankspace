package kr.io.blankspace.account.repository;

import kr.io.blankspace.account.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
        select t from Token t
        join fetch t.user u
        where t.tokenHash = :hash
            and t.tokenType = :type
            and t.usedAt is null
            and t.expiresAt > :now
    """)
    Optional<Token> findValidWithUser(String hash, Token.TokenType type, LocalDateTime now);
}