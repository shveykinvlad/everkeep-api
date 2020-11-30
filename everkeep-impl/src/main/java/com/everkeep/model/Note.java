package com.everkeep.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.everkeep.enums.NotePriority;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Accessors(chain = true)
public class Note {

    private static final String ID_SEQ = "note_id_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
    @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Exclude
    private Long id;

    private String title;

    private String text;

    @Enumerated(EnumType.STRING)
    private NotePriority priority;

    @CreatedBy
    @Column(updatable = false, nullable = false)
    private String username;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createTime;
}
