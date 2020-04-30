package com.everkeep.service;

import com.everkeep.model.Note;
import com.everkeep.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public List<Note> getAll() {
        return noteRepository.findAll();
    }

    public Note get(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note with id = " + id + " not found"));
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public Note update(Note note) {
        return noteRepository.save(note);
    }

    public void delete(Long id) {
        noteRepository.deleteById(id);
    }
}
