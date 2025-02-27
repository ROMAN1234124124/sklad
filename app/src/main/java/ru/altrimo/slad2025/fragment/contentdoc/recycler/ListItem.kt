package ru.altrimo.slad2025.fragment.contentdoc.recycler

import ru.altrimo.slad2025.network.responce.BarcodeContainer
import ru.altrimo.slad2025.network.responce.RowContainer

sealed class ListItem {
    data class ProductItem(val rowContainer: RowContainer) : ListItem()
    data class BarcodeItem(val barcodeItem: BarcodeContainer) : ListItem()
}