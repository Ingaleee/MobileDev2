package com.example.notepadingalee.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.notepadingalee.ActivityNavigator
import com.example.notepadingalee.R
import com.example.notepadingalee.Status
import com.example.notepadingalee.Storage
import java.util.logging.Logger

class NoteViewFragment constructor(currentId: Int): Fragment() {
    private lateinit var activityNavigator: ActivityNavigator;
    private var currentNoteId: Int;
    private val handler = Handler(Looper.getMainLooper());
    private lateinit var rootLayout: RelativeLayout
    init {
        currentNoteId = currentId;
    }

    private val autoSaver = object : Runnable {
        override fun run() {
            var currentNote = Storage.notes.firstOrNull() { note -> note.id == currentNoteId }
            currentNote?.content = view?.findViewById<EditText>(R.id.noteContentField)?.text.toString()
            handler.postDelayed(this, 100)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            activityNavigator = context as ActivityNavigator
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MyFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.note_view_fragment, container, false);

        var currentNote = Storage.notes.first() { note -> note.id == currentNoteId }
        view.findViewById<TextView>(R.id.noteTitleField)?.text = currentNote.title;
        view.findViewById<EditText>(R.id.noteContentField)?.setText(currentNote.content);

        view.findViewById<Button>(R.id.backToNotesBtn).setOnClickListener {
            onBackBtn(currentNote.folderId)
        }

        view.findViewById<Button>(R.id.removeNoteBtn).setOnClickListener {
            onRemoveClick();
        }
        handler.postDelayed(autoSaver, 100)
        return view;
    }

    override fun onDestroyView() {
        handler.removeCallbacks(autoSaver)
        super.onDestroyView()
    }

    private fun onBackBtn(folderId: Int){
        activityNavigator.openNotes(folderId)
    }
    private fun onRemoveClick(){
        var currentNote = Storage.notes.first {n -> n.id == currentNoteId}
        currentNote.status = Status.Removed;
        activityNavigator.openNotes(currentNote.folderId)
    }

    companion object{
        fun createByNote(noteId: Int): NoteViewFragment {
            return NoteViewFragment(noteId)
        }
    }
}