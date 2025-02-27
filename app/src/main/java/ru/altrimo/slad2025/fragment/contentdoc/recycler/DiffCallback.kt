package ru.altrimo.slad2025.fragment.contentdoc.recycler

import androidx.recyclerview.widget.DiffUtil

class DiffCallback(
    private val oldList: List<ListItem>,
    private val newList: List<ListItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem is ListItem.ProductItem && newItem is ListItem.ProductItem && oldItem.rowContainer == newItem.rowContainer ||
                oldItem is ListItem.BarcodeItem && newItem is ListItem.BarcodeItem && oldItem.barcodeItem == newItem.barcodeItem


    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem is ListItem.ProductItem && newItem is ListItem.ProductItem && oldItem.rowContainer.quantityFact == newItem.rowContainer.quantityFact

    }
}