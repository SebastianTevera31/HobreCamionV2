package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.promotions.StoreDiscount
import com.rfz.appflotal.data.model.promotions.toDomain
import com.rfz.appflotal.data.model.promotions.toEntity
import com.rfz.appflotal.data.network.service.promotions.LocalPromotionsDataSource
import com.rfz.appflotal.data.network.service.promotions.RemotePromotionsDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class PromotionsPage(
    val items: List<StoreDiscount>,
    val hasNextPage: Boolean
)

class PromotionsRepository @Inject constructor(
    private val remotePromotionsDataSource: RemotePromotionsDataSource,
    private val localPromotionsDataSource: LocalPromotionsDataSource,
    private val getTasksUseCase: GetTasksUseCase
) {

    /**
     * Consulta el endpoint y reemplaza el cache local completo con el resultado.
     * Es la única función que toca la red; la ejecuta el worker diario, no la UI.
     */
    suspend fun syncDiscounts(
        search: String = "",
        store: String = "",
        limit: Int = 50,
        pagesPerStore: Int = 20
    ): Result<Unit> {
        val token = getTasksUseCase().first().first().fld_token
        return remotePromotionsDataSource.getDiscounts(
            token = token,
            search = search,
            store = store,
            limit = limit,
            pagesPerStore = pagesPerStore
        ).map { response ->
            val entities = response.result.mapIndexed { index, discount -> discount.toEntity(index) }
            localPromotionsDataSource.refreshAll(entities)
        }
    }

    /**
     * Lee una página del cache local (fuente de verdad entre sincronizaciones), sin red.
     */
    suspend fun getPromotionsPage(
        page: Int,
        pageSize: Int,
        search: String = ""
    ): Result<PromotionsPage> {
        val offset = (page - 1) * pageSize
        return localPromotionsDataSource.getPage(search = search, limit = pageSize, offset = offset)
            .map { entities ->
                val total = localPromotionsDataSource.count(search)
                PromotionsPage(
                    items = entities.map { it.toDomain() },
                    hasNextPage = offset + entities.size < total
                )
            }
    }
}
