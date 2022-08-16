package com.bigri239.easymusic.utils

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.*

class UpdateDialog (private val areAvailableUpdateTypes : Array<Boolean>) : DialogFragment() {

    private var types = arrayOf<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val typesList = arrayListOf<String>()
            if (areAvailableUpdateTypes[0]) typesList.add("Alpha")
            if (areAvailableUpdateTypes[1]) typesList.add("Beta")
            if (areAvailableUpdateTypes[2]) typesList.add("Stable")
            types = typesList.toTypedArray()
            val builder = AlertDialog.Builder(it)
            var itemChosen = types[0]
            builder.setTitle("Download and install the latest version of our app!")
                .setSingleChoiceItems(types, -1) { _, item ->
                    itemChosen = types[item]
                }
                .setNegativeButton("Download") { dialog, id ->
                    Toast.makeText(activity, "Chosen version:  $itemChosen",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://bialger.com/easymusic/download_current_app.php" +
                                "?type=$itemChosen"))
                    startActivity(intent)
                }
                .setPositiveButton("Cancel") { _, id ->
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}