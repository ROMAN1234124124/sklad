package ru.altrimo.slad2025.fragment.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleListAdapter<T, VH : SimpleListAdapter.ViewHolder<T>>(
    private val items: List<T>
) : RecyclerView.Adapter<VH>() {

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
        holder.indexedConfig(items[position], position, items.size)
    }

    override fun getItemCount() = items.size

    fun getAllItems() = items

    abstract class ViewHolder<T>(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
        open fun indexedConfig(item: T, index: Int, size: Int) {}
    }
}