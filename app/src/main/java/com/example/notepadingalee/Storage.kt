package com.example.notepadingalee

import com.example.notepadingalee.fragments.NotesViewFragment

class Storage {
    companion object{
        var notes: MutableList<Note> = mutableListOf();
        var folders: MutableList<NoteFolder> = mutableListOf(NoteFolder(1, "Folder 1", Status.Active));
    }
}