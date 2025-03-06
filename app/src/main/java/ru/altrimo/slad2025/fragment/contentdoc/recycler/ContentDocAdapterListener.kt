package ru.altrimo.slad2025.fragment.contentdoc.recycler

interface ContentDocAdapterAction {

    fun actionDelBarcode(barcode: String)
    fun actionAllDelBarcode(guid: String)
    fun actionSelectProduct(guid: String)
    fun actionExpandable(guid: String)
}