package com.everkeep.model;

import java.time.OffsetDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class VerificationToken {

    private static final String ID_SEQ = "verification_token_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Exclude
    private Long id;

    private String value;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    @EqualsAndHashCode.Exclude
    private User user;

    private OffsetDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    private Action action;

    private boolean active = true;

    public enum Action {
        RESET_PASSWORD,
        CONFIRM_ACCOUNT,
        REFRESH_ACCESS
    }
}
