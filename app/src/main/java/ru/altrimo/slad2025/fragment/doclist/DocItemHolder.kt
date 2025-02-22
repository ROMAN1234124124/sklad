package ru.altrimo.slad2025.fragment.doclist


import ru.altrimo.slad2025.databinding.DocItemHolderBinding
import ru.altrimo.slad2025.fragment.base.SimpleListAdapter
import ru.altrimo.slad2025.network.responce.DocItem
import java.util.Locale

class DocItemHolder(
    private val binding: DocItemHolderBinding,
    private val onClicked: (item: DocItem) -> Unit
) : SimpleListAdapter.ViewHolder<DocItem>(binding.root) {

    override fun bind(item: DocItem) {
        binding.docNumberAndTypeDoc.text = String.format(
            Locale.getDefault(), "%s %s", item.docType, item.docNumber
        )
        binding.docStatusAndPercent.text = String.format(
            Locale.getDefault(), "%s: %d%%", item.getDocStatus(item.docStatus), item.docPercent
        )
        binding.docComment.text = item.docComment
        binding.docDate.text = item.docDate
        binding.root.setOnClickListener {
            onClicked.invoke(item)
        }
    }

}
