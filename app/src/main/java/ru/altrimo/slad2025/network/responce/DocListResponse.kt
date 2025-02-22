package ru.altrimo.slad2025.network.responce

import com.google.gson.annotations.SerializedName

data class DocListResponse(
    @SerializedName("docslist")
    val docItem: List<DocItem>,
    val result: String,
    val error: Error
)

data class DocItem(
    @SerializedName("doc_guid")
    val docGUID: String,
    @SerializedName("doc_number")
    val docNumber: String,
    @SerializedName("doc_date")
    val docDate: String,
    @SerializedName("doc_comment")
    val docComment: String,
    @SerializedName("doc_version")
    val docVersion: Int,
    @SerializedName("doc_type")
    val docType: String,
    @SerializedName("only_fact")
    val onlyFact: Boolean,
    @SerializedName("doc_status")
    val docStatus: String,
    @SerializedName("doc_procent")
    val docPercent: Int
) {

    fun getDocStatus(docType: String): String {
        return when (docType) {
            "1" -> "Новый"
            "2" -> "В работе"
            "3" -> "Обработан"
            else -> "error doc_status"
        }
    }

}