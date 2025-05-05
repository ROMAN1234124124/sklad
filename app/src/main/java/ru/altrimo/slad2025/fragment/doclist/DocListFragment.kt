package ru.altrimo.slad2025.fragment.doclist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.databinding.FragmentDocListBinding
import ru.altrimo.slad2025.databinding.ItemDocBinding
import ru.altrimo.slad2025.fragment.base.SimpleListAdapter
import ru.altrimo.slad2025.fragment.base.ViewBindingFragment
import ru.altrimo.slad2025.network.responce.DocItem
import ru.altrimo.slad2025.viewmodel.DocListViewModel

@AndroidEntryPoint
class DocListFragment : ViewBindingFragment<FragmentDocListBinding>() {

    override val inflaterDelegate by inflaterDelegate()
    private val viewModel: DocListViewModel by viewModels()

    override fun onInflationComplete() {
        setupObserve()
        requireActivity().onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showConfirmationDialog(getString(R.string.close_app)) {
                        requireActivity().finish()
                    }
                }
            })
        binding.refresher.setOnRefreshListener {
            viewModel.docList()
        }
    }

    private fun setupObserve() {
        viewModel.viewResult.observe(viewLifecycleOwner) {
            initAdapter(it)
        }

        viewModel.viewShowError.observe(viewLifecycleOwner) {
            showError(it)
        }

        viewModel.viewShowLoading.observe(viewLifecycleOwner) {
            binding.refresher.isRefreshing = it
        }

        viewModel.docOpen.observe(viewLifecycleOwner) {
            if (it.userMessage.isBlank()) {
                navigateToDocList(it.docGUID, it.docVersion)
            } else {
                showNextDialog(message = it.userMessage) {
                    navigateToDocList(it.docGUID, it.docVersion)
                }
            }
        }
    }

    private fun navigateToDocList(docGUID: String, docVersion: Int) {
        findNavController().navigate(
            DocListFragmentDirections.actionDocListToContentDoc(
                docGUID = docGUID,
                docVersion = docVersion
            )
        )
    }

    private fun initAdapter(item: List<DocItem>) {
        val isNotEmpty = item.isNotEmpty()
        binding.viewEmpty.root.isVisible = !isNotEmpty
        binding.recycler.isVisible = isNotEmpty
        if (isNotEmpty) {
            binding.recycler.adapter = null
            binding.recycler.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.recycler.adapter =
                object : SimpleListAdapter<DocItem, DocItemHolder>(item) {
                    override fun onCreateViewHolder(
                        parent: ViewGroup, viewType: Int
                    ): DocItemHolder {
                        val binding = ItemDocBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false
                        )
                        return DocItemHolder(
                            binding
                        ) {
                            viewModel.openDoc(docGUID = it.docGUID, docVersion = it.docVersion)
                        }
                    }
                }
        }

    }


}