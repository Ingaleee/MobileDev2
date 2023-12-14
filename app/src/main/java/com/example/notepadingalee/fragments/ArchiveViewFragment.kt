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

class ArchiveViewFragment : Fragment() {
    private lateinit var activityNavigator: ActivityNavigator
    private lateinit var rootLayout: RelativeLayout
    private var currentItems: Int = 0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.archive_view_fragment, container, false);

        rootLayout = view.findViewById(R.id.rootLayout)

        rootLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                if (savedInstanceState == null) {

                    for (folder in Storage.folders) {
                        if (folder.status == Status.Removed){
                            showFolder(folder.id)
                        }
                    }

                    for (note in Storage.notes) {
                        if (note.status == Status.Removed){
                            showNote(note.id);
                        }
                    }
                }
            }
        })

        var backBtn = view.findViewById<Button>(R.id.backArchiveBtn)
        backBtn.setOnClickListener {
            activityNavigator.openFolders()
        }
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

    private fun showFolder(id: Int){
        var currentFolder = Storage.folders.first { f -> f.id == id };
        val inflater = LayoutInflater.from(requireContext())

        val itemWidth = (rootLayout.width * 0.3).toInt()
        val itemHeight = (rootLayout.height * 0.2).toInt()

        val inflatedLayout = inflater.inflate(R.layout.note_folder_layout, null)

        val layoutParams = RelativeLayout.LayoutParams(
            itemWidth,
            itemHeight
        )
        inflatedLayout.findViewById<TextView>(R.id.folderNameTextView).text = currentFolder.name

        val folderBtn = inflatedLayout.findViewById<ImageButton>(R.id.imageButton3)
        folderBtn.setOnLongClickListener{
            onFolderRecover(currentFolder)
            true
        }

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)

        layoutParams.leftMargin = (currentItems % 3) * itemWidth
        layoutParams.topMargin = (currentItems / 3) * itemHeight
        inflatedLayout.layoutParams = layoutParams

        currentItems++;
        rootLayout.addView(inflatedLayout)
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
        noteBtn.setOnLongClickListener {
            onNoteRecover(currentNote)
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

    private fun onNoteRecover(note: Note){
        val input = EditText(requireContext())
        input.setText(note.title);

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Recover folder?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            note.status = Status.Active
            activityNavigator.openFolders();
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") {dialog, _ ->
            dialog.dismiss();
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun onFolderRecover(folder: NoteFolder){
        val input = EditText(requireContext())
        input.setText(folder.name);

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Recover folder?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            folder.status = Status.Active
            activityNavigator.openFolders();
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") {dialog, _ ->
            dialog.dismiss();
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}