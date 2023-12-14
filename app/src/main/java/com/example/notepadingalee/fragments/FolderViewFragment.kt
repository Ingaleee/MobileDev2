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
import com.example.notepadingalee.NoteFolder
import com.example.notepadingalee.R
import com.example.notepadingalee.Status
import com.example.notepadingalee.Storage

class FolderViewFragment : Fragment() {
    private lateinit var activityNavigator: ActivityNavigator
//    private var folders: MutableList<NoteFolder> = mutableListOf();
    private lateinit var rootLayout: RelativeLayout
    private var currentItems: Int = 0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.folder_view_fragment, container, false);

        rootLayout = view.findViewById(R.id.rootLayout)

        rootLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                if (savedInstanceState == null) {

                    for (folder in Storage.folders) {
                        if (folder.status == Status.Active){
                            showFolder(folder.id)
                        }
                    }
                }
            }
        })

        var addBtn = view.findViewById<Button>(R.id.addBtn)
        addBtn.setOnClickListener {
            onAddClick(addBtn)
        }

        var archiveBtn = view.findViewById<Button>(R.id.archiveBtn);
        archiveBtn.setOnClickListener {
            onArchiveClick()
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

    private fun onFolderClick(view: View, folder: NoteFolder){
        activityNavigator.openNotes(folder.id)
    }

    private fun onArchiveClick(){
        activityNavigator.openArchive()
    }

    private fun onFolderRename(textView: TextView, folder: NoteFolder){
        val input = EditText(requireContext())
        input.setText(folder.name);

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Input folder name")
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            folder.name = input.text.toString();
            textView.text = folder.name
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") {dialog, _ ->
            dialog.dismiss();
        }

        val alertDialog = builder.create()
        alertDialog.show()
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
        folderBtn.setOnClickListener {
            onFolderClick(folderBtn, currentFolder)
        }

        val folderTxt = inflatedLayout.findViewById<TextView>(R.id.folderNameTextView)
        folderBtn.setOnLongClickListener{
            onFolderRename(folderTxt, currentFolder)
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

    private fun onAddClick(view: View) {
        var title: String;
        val input = EditText(requireContext())
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(input)
        builder.setTitle("Input folder name")
        builder.setPositiveButton("OK") { dialog, _ ->
            title = input.text.toString()

            val newFolderId = Storage.folders.size + 1
            val newFolder = NoteFolder(newFolderId, title, Status.Active)
            Storage.folders.add(newFolder)

            showFolder(newFolderId);
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}