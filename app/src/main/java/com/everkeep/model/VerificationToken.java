package com.everkeep.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {

    private static final String ID_SEQ = "verification_token_id_sequence";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    private Long id;

    @EqualsAndHashCode.Include
    private String hashValue;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private OffsetDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Builder.Default
    private boolean active = true;

    public enum Action {
        PASSWORD_RESET,
        ACCOUNT_CONFIRMATION,
        SESSION_REFRESH
    }
}
