package com.everkeep.service;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.everkeep.model.Note;
import com.everkeep.repository.NoteRepository;
import com.everkeep.utils.SpecificationUtils;

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
                .orElseThrow(() -> new EntityNotFoundException("Note with id = %d not found".formatted(id)));
    }

    public List<Note> search(String value) {
        return noteRepository.findAll(isContainedInTitleOrText(value, userService.getAuthenticatedUsername()));
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public Note update(Note note) {
        if (noteRepository.findByIdAndUsername(note.getId(), userService.getAuthenticatedUsername()).isEmpty()) {
            throw new AccessDeniedException("%s have no permissions for note id = %d"
                    .formatted(userService.getAuthenticatedUsername(), note.getId()));
        }
        return noteRepository.save(note);
    }

    public void delete(Long id) {
        noteRepository.deleteByIdAndUsername(id, userService.getAuthenticatedUsername());
    }

    private Specification<Note> isContainedInTitleOrText(String value, String username) {
        return Specification.where(isContainedInTitleOrText(value))
                .and(SpecificationUtils.isEqualToAttribute(username, Note.Fields.username));
    }

    private Specification<Note> isContainedInTitleOrText(String value) {
        return SpecificationUtils.isContainedInAttributes(value, Arrays.asList(Note.Fields.title, Note.Fields.text));
    }
}
