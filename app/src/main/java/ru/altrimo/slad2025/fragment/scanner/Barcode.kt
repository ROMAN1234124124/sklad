package ru.altrimo.slad2025.fragment.scanner

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Barcode(
    var list: List<String>
) : Parcelable
