package ua.andrii.andrushchenko.gimmepictures.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

inline fun EditText.compareTextBeforeAndAfter(crossinline listener: (Boolean) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        var initial = ""
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            initial = s.toString()
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(s: Editable?) {
            listener((s.toString().contentEquals(initial)).not())
        }
    })
}