package ru.altrimo.slad2025.fragment.contentdoc.recycler

import androidx.recyclerview.widget.DiffUtil

class DiffCallback : DiffUtil.ItemCallback<ListItem>() {

    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return when {
            oldItem is ListItem.ProductItem && newItem is ListItem.ProductItem -> {
                oldItem.rowContainer.rowGUID == newItem.rowContainer.rowGUID
            }

            oldItem is ListItem.BarcodeItem && newItem is ListItem.BarcodeItem -> {
                oldItem.barcodeItem.rowGuid == newItem.barcodeItem.rowGuid
            }

            else -> true
        }
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return when {
            oldItem is ListItem.ProductItem && newItem is ListItem.ProductItem -> {
                oldItem.rowContainer.quantityFact == newItem.rowContainer.quantityFact &&
                        oldItem.rowContainer.isSelected == newItem.rowContainer.isSelected
            }

            else -> true
        }
    }
}
