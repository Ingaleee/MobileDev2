package com.example.notepadingalee.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.notepadingalee.ActivityNavigator
import com.example.notepadingalee.Note
import com.example.notepadingalee.NoteFolder
import com.example.notepadingalee.R
import com.example.notepadingalee.Status
import com.example.notepadingalee.Storage

class NotesViewFragment constructor(currentFolderId: Int) : Fragment() {
    private lateinit var activityNavigator: ActivityNavigator;
    private lateinit var rootLayout: RelativeLayout
    private var currentItems: Int = 0;

    private var currentFolderId: Int;
    init {
        this.currentFolderId = currentFolderId;
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.notes_view_fragment, container, false);

        rootLayout = view.findViewById(R.id.notesRootLayout)
        rootLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                if (savedInstanceState == null) {
                    for (note in Storage.notes) {
                        var isActive = note.status == Status.Active
                        if (note.folderId == currentFolderId && isActive){
                            showNote(note.id);
                        }
                    }
                }
            }
        })

        var addBtn = view.findViewById<Button>(R.id.addNoteBtn)
        addBtn.setOnClickListener {
            onAddClick(addBtn)
        }

        view.findViewById<Button>(R.id.backNoteBtn).setOnClickListener {
            onBackBtn()
        }

        view.findViewById<TextView>(R.id.removeFolderBtn).setOnClickListener{
            onRemoveClick();
        };
        return view;
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            activityNavigator = context as ActivityNavigator
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MyFragmentListener")
        }
    }

    private fun showNote(id: Int){
        var currentNote = Storage.notes[id - 1];
        val inflater = LayoutInflater.from(requireContext())

        val itemWidth = (rootLayout.width * 0.3).toInt()
        val itemHeight = (rootLayout.height * 0.2).toInt()

        val inflatedLayout = inflater.inflate(R.layout.note_layout, null)

        val layoutParams = RelativeLayout.LayoutParams(
            itemWidth,
            itemHeight
        )
        inflatedLayout.findViewById<TextView>(R.id.noteTitleTextView).text = currentNote.title
        var noteBtn = inflatedLayout.findViewById<ImageButton>(R.id.noteImageButton)
        noteBtn.setOnClickListener {
            onNoteClick(currentNote)
        }

        val noteTxt = inflatedLayout.findViewById<TextView>(R.id.noteTitleTextView)
        noteBtn.setOnLongClickListener {
            onNoteRename(noteTxt, currentNote)
            true
        }

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)

        layoutParams.leftMargin = ((currentItems) % 3) * itemWidth
        layoutParams.topMargin = ((currentItems) / 3) * itemHeight
        inflatedLayout.layoutParams = layoutParams

        currentItems++
        rootLayout.addView(inflatedLayout)
    }

    private fun onNoteClick(note: Note){
        activityNavigator.openNote(note.id)
    }

    private fun onBackBtn(){
        activityNavigator.openFolders();
    }

    private fun onRemoveClick(){
        for (note in Storage.notes){
            if (note.folderId == currentFolderId){
                note.status == Status.Removed
            }
        }

        var currentFolder = Storage.folders.first { folder -> folder.id == currentFolderId };
        currentFolder.status = Status.Removed;
        activityNavigator.openFolders()
    }

    private fun onNoteRename(textView: TextView, note: Note){
        val input = EditText(requireContext())
        input.setText(note.title);

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Input folder name")
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            note.title = input.text.toString();
            textView.text = note.title
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") {dialog, _ ->
            dialog.dismiss();
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }


    private fun onAddClick(view: View) {
        var title: String;
        val input = EditText(requireContext())
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(input)
        builder.setTitle("Input note title")
        builder.setPositiveButton("OK") { dialog, _ ->
            title = input.text.toString()

            val newNoteId = Storage.notes.size + 1
            val newNote = Note(newNoteId, title, Status.Active,"", currentFolderId)
            Storage.notes.add(newNote)

            showNote(newNoteId)
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    companion object{
        fun createByFolder(folderId: Int): NotesViewFragment {
            return NotesViewFragment(folderId)
        }
    }
}