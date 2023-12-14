package com.example.notepadingalee

data class NoteFolder(
    val id: Int,
    var name: String,
    var status: Status
)

data class Note(
    val id: Int,
    var title: String,
    var status: Status,
    var content: String,
    val folderId: Int
)