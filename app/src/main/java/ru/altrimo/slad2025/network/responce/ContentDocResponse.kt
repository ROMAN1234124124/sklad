package ru.altrimo.slad2025.network.responce

import com.google.gson.annotations.SerializedName

data class ContentDocResponse(
    @SerializedName("doc_modeFilling")
    val docModeFilling: Int,
    @SerializedName("dynamic_html_text")
    val dynamicHtmlText: String,
    @SerializedName("doc_scanbutton")
    val isHandScanMode: Boolean = true,
    @SerializedName("rows_content")
    val listRowContainer: List<RowContainer>,
    val result: String,
    val error: Error
)

data class RowContainer(
    @SerializedName("row_guid")
    val rowGUID: String,
    var isSelected: Boolean = false,
    var isExpandable: Boolean = false,
    @SerializedName("row_version")
    val rowVersion: Int,
    @SerializedName("row_number")
    val rowNumber: Int,
    val product: String,
    @SerializedName("product_guid")
    val productGUID: String,
    val quantity: Int,
    @SerializedName("quantity_fact")
    val quantityFact: Int,
    val pack: String,
    @SerializedName("pack_guid")
    val packGUID: String,
    @SerializedName("quantity_pack")
    val quantityPack: Int,
    @SerializedName("quantity_pack_fact")
    val quantityPackFact: Int,
    @SerializedName("Barcodes")
    val listBarcode: List<BarcodeContainer>
)

data class BarcodeContainer(
    @SerializedName("row_guid")
    val rowGuid: String,
    val product: String,
    @SerializedName("product_guid")
    val productGuid: String,
    @SerializedName("barCode")
    val barcode: String,
    val quantity: String,
)