package ru.altrimo.slad2025.fragment.doclist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.databinding.DocItemHolderBinding
import ru.altrimo.slad2025.databinding.DocListFragmentBinding
import ru.altrimo.slad2025.fragment.base.SimpleListAdapter
import ru.altrimo.slad2025.fragment.base.ViewBindingFragment
import ru.altrimo.slad2025.network.responce.DocItem
import ru.altrimo.slad2025.viewmodel.DocListViewModel

@AndroidEntryPoint
class DocListFragment : ViewBindingFragment<DocListFragmentBinding>() {

    override val inflaterDelegate by inflaterDelegate()
    private val viewModel: DocListViewModel by viewModels()

    override fun onInflationComplete() {
        setupObserve()
        binding.refresher.setOnRefreshListener {
            viewModel.docList()
        }
    }

    private fun setupObserve() {
        viewModel.viewResult.observe(viewLifecycleOwner) {
            binding.refresher.isRefreshing = false
            initAdapter(it)
        }

        viewModel.viewShowError.observe(viewLifecycleOwner) {
            showError(it)
        }

        viewModel.viewShowLoading.observe(viewLifecycleOwner) {
            showProgress(it)
        }
    }


    private fun initAdapter(item: List<DocItem>) {
        binding.recycler.adapter = null
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recycler.adapter =
            object : SimpleListAdapter<DocItem, DocItemHolder>(item) {
                override fun onCreateViewHolder(
                    parent: ViewGroup, viewType: Int
                ): DocItemHolder {
                    val binding = DocItemHolderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                    return DocItemHolder(
                        binding
                    ) {

                    }
                }
            }
    }

}