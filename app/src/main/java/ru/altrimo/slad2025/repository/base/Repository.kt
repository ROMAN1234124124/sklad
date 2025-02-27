package ru.altrimo.slad2025.repository.base

import ru.altrimo.slad2025.network.base.BaseWebServiceApi
import ru.altrimo.slad2025.network.base.SafeApiCall


abstract class Repository(private val api: BaseWebServiceApi) : SafeApiCall {
}