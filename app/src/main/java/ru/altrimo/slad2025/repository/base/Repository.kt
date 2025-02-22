package ru.altrimo.slad2025.repository.base

import ru.altrimo.slad2025.network.BaseWebServiceApi
import ru.altrimo.slad2025.network.SafeApiCall


abstract class Repository(private val api: BaseWebServiceApi) : SafeApiCall {
}