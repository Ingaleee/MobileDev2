package com.example.notepadingalee

interface ActivityNavigator {
    fun openFolders();
    fun openNotes(folderId: Int);
    fun openNote(noteId: Int);
    fun openArchive();
}