package ua.andrii.andrushchenko.gimmepictures.util

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ua.andrii.andrushchenko.gimmepictures.R

fun Context.showAlertDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    listener: DialogInterface.OnClickListener
) {
    MaterialAlertDialogBuilder(this).run {
        setTitle(title)
        setMessage(message)
        setPositiveButton(R.string.yes, listener)
        setNegativeButton(R.string.no, null)
        show()
    }
}

fun Context.showInfoAlertDialogWithoutTitle(message: String) {
    MaterialAlertDialogBuilder(this).run {
        setMessage(message)
        show()
    }
}

fun Context.showAlertDialogWithSelectionsList(
    @StringRes title: Int,
    choicesList: Array<String>,
    listener: DialogInterface.OnClickListener
) {
    MaterialAlertDialogBuilder(this).run {
        setTitle(title)
        setItems(choicesList, listener)
        create()
        show()
    }
}

fun Context.showAlertDialogWithRadioButtons(
    @StringRes title: Int,
    choicesList: Array<String>,
    currentSelection: Int,
    listener: DialogInterface.OnClickListener
) {
    MaterialAlertDialogBuilder(this).run {
        setTitle(title)
        setSingleChoiceItems(choicesList, currentSelection, listener)
        create()
        show()
    }
}

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    message?.let {
        Toast.makeText(this, it, duration).show()
    }
}
