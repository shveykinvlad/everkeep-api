package com.everkeep.service;

import java.util.List;
import javax.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.everkeep.model.Note;
import com.everkeep.repository.NoteRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;

    public List<Note> getAll() {
        return noteRepository.findByUsername(userService.getAuthenticatedUsername());
    }

    public Note get(Long id) {
        return noteRepository.findByIdAndUsername(id, userService.getAuthenticatedUsername())
                .orElseThrow(() -> new EntityNotFoundException("Note with id = " + id + " not found"));
    }

    public List<Note> get(String title) {
        return noteRepository.findByTitleContainsAndUsername(title, userService.getAuthenticatedUsername());
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public Note update(Note note) {
        if (noteRepository.findByIdAndUsername(note.getId(), userService.getAuthenticatedUsername())
                .isEmpty()) {
            throw new AccessDeniedException(userService.getAuthenticatedUsername() + " have no permissions for note id = " + note.getId());
        }
        return noteRepository.save(note);
    }

    public void delete(Long id) {
        noteRepository.deleteByIdAndUsername(id, userService.getAuthenticatedUsername());
    }
}
