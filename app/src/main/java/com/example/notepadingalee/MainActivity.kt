package com.example.notepadingalee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notepadingalee.fragments.ArchiveViewFragment
import com.example.notepadingalee.fragments.FolderViewFragment
import com.example.notepadingalee.fragments.NoteViewFragment
import com.example.notepadingalee.fragments.NotesViewFragment

class MainActivity : AppCompatActivity(), ActivityNavigator {
    private lateinit var location: Location;
//    private lateinit var foldersFragment: FolderViewFragment;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        location = Location.Folders;
        openFolders();
        setContentView(R.layout.activity_main)
    }

    override fun openFolders() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, FolderViewFragment())
        transaction.addToBackStack(null)
        transaction.commit()

        location = Location.Folders
    }

    override fun openNotes(folderId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, NotesViewFragment.createByFolder(folderId))
        transaction.commit()

        location = Location.Notes;
    }

    override fun openNote(noteId: Int){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, NoteViewFragment.createByNote(noteId))
        transaction.commit()

        location = Location.Note
    }

    override fun openArchive() {
        val transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, ArchiveViewFragment())
        transaction.commit()

        location = Location.Archive
    }
}