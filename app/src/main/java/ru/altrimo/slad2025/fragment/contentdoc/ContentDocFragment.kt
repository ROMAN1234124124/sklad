package ru.altrimo.slad2025.fragment.contentdoc

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.altrimo.slad2025.R
import ru.altrimo.slad2025.common.BarcodeReceiver
import ru.altrimo.slad2025.common.setHtmlText
import ru.altrimo.slad2025.databinding.FragmentContentDocBinding
import ru.altrimo.slad2025.fragment.base.ViewBindingFragment
import ru.altrimo.slad2025.fragment.contentdoc.recycler.ContentDocAdapter
import ru.altrimo.slad2025.fragment.contentdoc.recycler.ContentDocAdapterAction
import ru.altrimo.slad2025.fragment.contentdoc.recycler.ListItem
import ru.altrimo.slad2025.fragment.scanner.BarcodeScannerFragment
import ru.altrimo.slad2025.network.request.Barcode
import ru.altrimo.slad2025.network.responce.RowContainer
import ru.altrimo.slad2025.viewmodel.ContentDocViewModel

@AndroidEntryPoint
class ContentDocFragment : ViewBindingFragment<FragmentContentDocBinding>(),
    ContentDocAdapterAction {

    override val inflaterDelegate by inflaterDelegate()
    private val viewModel: ContentDocViewModel by viewModels()
    private val args: ContentDocFragmentArgs by navArgs()
    private val permissionLauncher = permissionLauncher(::checkPermissionCamera)
    private lateinit var adapter: ContentDocAdapter

    private val barcodeReceiver = object : BarcodeReceiver() {
        override fun onBarcodeReceive(
            context: Context,
            barcode: ru.altrimo.slad2025.common.Barcode
        ) {
            viewModel.searchBarcode(
                docVersion = args.docVersion,
                docGUID = args.docGUID,
                barcodeList = listOf(barcode).map {
                    Barcode(it.data)
                }
            )
        }
    }

    override fun onInflationComplete() {
        setupAdapter()
        setupObserve()
        runCameraScanner()
        refreshData()
        setupCameraScannerResultListener()
        setupBackPressedDispatcher()
        binding.actionCamera.setOnClickListener {
            viewModel.changeShowCamera()
        }
        binding.refresher.setOnRefreshListener {
            refreshData()
        }
        lifecycle.addObserver(barcodeReceiver.registerLifecycleEventObserver(requireContext()))
    }

    private fun setupBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showConfirmationDialog(getString(R.string.confirm_close_doc)) {
                        viewModel.closeDoc(
                            docVersion = args.docVersion,
                            docGUID = args.docGUID
                        )
                    }
                }
            })
    }


    private fun setupCameraScannerResultListener() {
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


    private fun setupAdapter() {
        adapter = ContentDocAdapter(this)
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recycler.adapter = adapter
    }


    private fun refreshData() {
        viewModel.contentDoc(
            docVersion = args.docVersion,
            docGUID = args.docGUID
        )
    }


    private fun runCameraScanner() {
        if (requireActivity().applicationContext.checkSelfPermission(Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            childFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.child_fragment_container, BarcodeScannerFragment())
            }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun checkPermissionCamera(isGranted: Boolean) {
        if (isGranted) {
            runCameraScanner()
        } else {
            showError(getString(R.string.error_camera_permission))
        }
    }

    private fun setupObserve() {
        viewModel.viewResult.observe(viewLifecycleOwner) {
            refreshItemsAdapter(it.listRowContainer)
            binding.dynamicText.setHtmlText(it.dynamicHtmlText)
        }

        viewModel.viewShowError.observe(viewLifecycleOwner) {
            playSoundError()
            showError(it)
        }

        viewModel.viewShowLoading.observe(viewLifecycleOwner) {
            binding.refresher.isRefreshing = it
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
        viewModel.deleteBarcodeAll.observe(viewLifecycleOwner) {
            refreshData()
        }
        viewModel.closeDoc.observe(viewLifecycleOwner) {
            if (it.userMessage.isBlank()) {
                findNavController().popBackStack()
            } else {
                showNextDialog(message = it.userMessage) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun refreshItemsAdapter(item: List<RowContainer>?, rowGUID: String? = null) {
        val items: MutableList<ListItem> = mutableListOf()
        item?.forEach { product ->
            val updateProduct = if (rowGUID != null) {
                product.copy(isSelected = product.rowGUID == rowGUID)
            } else {
                product.copy(isSelected = product.rowGUID == viewModel.selectedProductGUID.value)
            }
            items.add(ListItem.ProductItem(updateProduct))
            if (viewModel.expandableProduct[updateProduct.rowGUID] == false) {
                updateProduct.listBarcode.forEach { barcode ->
                    items.add(ListItem.BarcodeItem(barcode))
                }
            }
        }
        adapter.submitList(items)
    }

    override fun actionDelBarcode(barcode: String) {
        showConfirmationDialog(getString(R.string.del_barcode_confirm_message)) {
            viewModel.deleteBarcode(
                docVersion = args.docVersion,
                docGUID = args.docGUID,
                barcode = barcode
            )
        }
    }

    override fun actionAllDelBarcode(guid: String) {
        showConfirmationDialog(getString(R.string.del_all_barcode_confirm_message)) {
            viewModel.deleteBarcodeAll(
                docVersion = args.docVersion,
                docGUID = args.docGUID,
                rowGUID = guid
            )
        }
    }

    override fun actionSelectProduct(guid: String) {
        viewModel.selectedProductGUID.value = guid
        refreshItemsAdapter(viewModel.viewResult.value?.listRowContainer, guid)
    }

    override fun actionExpandable(guid: String) {
        val isExpandable = viewModel.expandableProduct[guid] ?: true
        viewModel.expandableProduct[guid] = !isExpandable
        refreshItemsAdapter(viewModel.viewResult.value?.listRowContainer)
    }

}
