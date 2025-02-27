package ru.altrimo.slad2025.fragment.contentdoc.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.altrimo.slad2025.databinding.ItemContentBarcodeBinding
import ru.altrimo.slad2025.databinding.ItemContentProductBinding
import java.util.Locale


const val TYPE_PRODUCT = 0
const val TYPE_BARCODE = 1

class ContentDocAdapter(
    private val actionDelBarcode: (guid: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ListItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_PRODUCT) {
            ProductViewHolder(
                ItemContentProductBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            BarcodeViewHolder(
                ItemContentBarcodeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.ProductItem -> TYPE_PRODUCT
            is ListItem.BarcodeItem -> TYPE_BARCODE
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> holder.bind(items[position] as ListItem.ProductItem)
            is BarcodeViewHolder -> holder.bind(items[position] as ListItem.BarcodeItem)
        }
    }

    fun setItems(newItems: List<ListItem>) {
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    inner class BarcodeViewHolder(private val binding: ItemContentBarcodeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(barcode: ListItem.BarcodeItem) {
            binding.barcode.text = barcode.barcodeItem.barcode
            binding.count.text =
                String.format(Locale.getDefault(), "%sшт", barcode.barcodeItem.quantity)
            binding.actionDelBarcode.setOnClickListener {
                actionDelBarcode.invoke(barcode.barcodeItem.barcode)
            }
        }
    }

    inner class ProductViewHolder(private val binding: ItemContentProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ListItem.ProductItem) {
            binding.product.text = product.rowContainer.product
            binding.count.text = String.format(
                Locale.getDefault(),
                "Факт: %sшт\nПлан: %sшт",
                product.rowContainer.quantityFact,
                product.rowContainer.quantity
            )
        }
    }

}






