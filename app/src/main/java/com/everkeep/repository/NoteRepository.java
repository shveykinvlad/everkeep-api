package com.everkeep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.everkeep.model.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long>, JpaSpecificationExecutor<Note> {

    List<Note> findByUsername(String username);

    Optional<Note> findByIdAndUsername(Long id, String username);

    void deleteByIdAndUsername(Long id, String username);
}
