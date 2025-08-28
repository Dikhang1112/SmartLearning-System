package com.smartStudy.repositories;

import com.smartStudy.pojo.Note;

import java.util.List;
import java.util.Map;

public interface NoteRepository {
    Note addOrUpdate(Note no);
    List<Note> getNotes(Map<String,String> params);

    Note getNoteById(int id);
    void deleteNote(int nodeId);

}
