package ru.altrimo.slad2025.common

import android.text.Editable
import android.text.Html
import android.widget.TextView

fun TextView.setHtmlText(htmlText: String) {
    this.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
}

fun String.toEditable() : Editable =
    Editable.Factory.getInstance().newEditable(this)