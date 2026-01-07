package kr.io.blankspace.repository.account;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "user_tbl")
class DummyEntityForJpa {
    @Id
    @Column(name = "user_id")
    private String id;
}