package ru.altrimo.slad2025.fragment.contentdoc.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.databinding.ItemContentBarcodeBinding
import ru.altrimo.slad2025.databinding.ItemContentProductBinding
import java.util.Locale


const val TYPE_PRODUCT = 0
const val TYPE_BARCODE = 1

class ContentDocAdapter(
    private val contentDocAdapterAction: ContentDocAdapterAction
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(DiffCallback()) {

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
        return when (getItem(position)) {
            is ListItem.ProductItem -> TYPE_PRODUCT
            is ListItem.BarcodeItem -> TYPE_BARCODE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> holder.bind(getItem(position) as ListItem.ProductItem)
            is BarcodeViewHolder -> holder.bind(getItem(position) as ListItem.BarcodeItem)
        }
    }

    inner class BarcodeViewHolder(private val binding: ItemContentBarcodeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(barcode: ListItem.BarcodeItem) {
            binding.barcode.text = barcode.barcodeItem.barcode
            binding.count.text =
                String.format(Locale.getDefault(), "%sшт", barcode.barcodeItem.quantity)
            binding.actionDelBarcode.setOnClickListener {
                contentDocAdapterAction.actionDelBarcode(barcode.barcodeItem.barcode)
            }
        }
    }

    inner class ProductViewHolder(private val binding: ItemContentProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ListItem.ProductItem) {
            if (product.rowContainer.isSelected) {
                binding.cardView.setBackgroundResource(R.color.accent)
            } else {
                binding.cardView.setBackgroundResource(R.color.white)
            }
            binding.actionExpandable.isVisible = product.rowContainer.listBarcode.isNotEmpty()
            binding.product.text = product.rowContainer.product
            binding.count.text = String.format(
                Locale.getDefault(),
                "Факт: %sшт    План: %sшт",
                product.rowContainer.quantityFact,
                product.rowContainer.quantity
            )
            binding.actionAllDelete.setOnClickListener {
                contentDocAdapterAction.actionAllDelBarcode(product.rowContainer.rowGUID)
            }
            binding.root.setOnClickListener {
                contentDocAdapterAction.actionSelectProduct(product.rowContainer.rowGUID)
            }
            binding.actionExpandable.setOnClickListener {
                contentDocAdapterAction.actionExpandable(product.rowContainer.rowGUID)
            }
        }
    }

}






