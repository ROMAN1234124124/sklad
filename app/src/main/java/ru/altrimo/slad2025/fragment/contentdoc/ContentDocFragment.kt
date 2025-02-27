package ru.altrimo.slad2025.fragment.contentdoc

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.databinding.FragmentContentDocBinding
import ru.altrimo.slad2025.fragment.base.ViewBindingFragment
import ru.altrimo.slad2025.fragment.contentdoc.recycler.ContentDocAdapter
import ru.altrimo.slad2025.fragment.contentdoc.recycler.ListItem
import ru.altrimo.slad2025.fragment.scanner.BarcodeScannerFragment
import ru.altrimo.slad2025.network.request.Barcode
import ru.altrimo.slad2025.network.responce.RowContainer
import ru.altrimo.slad2025.viewmodel.ContentDocViewModel

@AndroidEntryPoint
class ContentDocFragment : ViewBindingFragment<FragmentContentDocBinding>() {

    override val inflaterDelegate by inflaterDelegate()
    private val viewModel: ContentDocViewModel by viewModels()
    private val args: ContentDocFragmentArgs by navArgs()
    private val permissionLauncher = permissionLauncher(::checkPermissionCamera)
    private lateinit var adapter: ContentDocAdapter

    override fun onInflationComplete() {
        runScanner()
        binding.actionCamera.setOnClickListener {
            viewModel.changeShowCamera()
        }
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapter = ContentDocAdapter {
            showDialog(getString(R.string.del_barcode_confim_message)) {
                viewModel.deleteBarcode(
                    docVersion = args.docVersion,
                    docGUID = args.docGUID,
                    barcode = it
                )
            }
        }
        binding.recycler.adapter = adapter

        binding.refresher.setOnRefreshListener {
            refreshData()
        }
        setupObserve()
        refreshData()
        childFragmentManager.setFragmentResultListener(
            BarcodeScannerFragment.SCAN_REQUEST,
            viewLifecycleOwner
        ) { _, result ->
            result.getStringArrayList(BarcodeScannerFragment.SCAN_RESULT)?.let {
                viewModel.searchBarcode(
                    docVersion = args.docVersion,
                    docGUID = args.docGUID,
                    barcodeList = it.map { barcode -> Barcode(barcode) }
                )
            }
        }
    }

    private fun refreshData() {
        viewModel.contentDoc(
            docVersion = args.docVersion,
            docGUID = args.docGUID
        )
    }


    private fun runScanner() {
        if (requireActivity().applicationContext.checkSelfPermission(Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            childFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.child_fragment_container, BarcodeScannerFragment())
            }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun checkPermissionCamera(isGranted: Boolean) {
        if (isGranted) {
            runScanner()
        } else {
            showError(getString(R.string.error_camera_permission))
        }
    }

    private fun setupObserve() {
        viewModel.viewResult.observe(viewLifecycleOwner) {
            addItemsAdapter(it.listRowContainer)
        }

        viewModel.viewShowError.observe(viewLifecycleOwner) {
            showError(it)
        }

        viewModel.viewShowLoading.observe(viewLifecycleOwner) {
            binding.refresher.isRefreshing = it
            showProgress(it)
        }
        viewModel.deleteBarcode.observe(viewLifecycleOwner) {
            refreshData()
        }
        viewModel.isShowCamera.observe(viewLifecycleOwner) {
            binding.childFragmentContainer.isVisible = it
        }
        viewModel.searchBarcode.observe(viewLifecycleOwner) {
            refreshData()
        }
    }

    private fun addItemsAdapter(item: List<RowContainer>) {
        val items: MutableList<ListItem> = mutableListOf()
        item.forEach { product ->
            items.add(ListItem.ProductItem(product))
            product.listBarcode.forEach { barcode ->
                items.add(ListItem.BarcodeItem(barcode))
            }
        }
        adapter.setItems(items)
    }


}

