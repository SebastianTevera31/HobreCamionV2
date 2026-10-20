package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.promotions.PromotionCategory
import com.rfz.appflotal.data.model.promotions.StoreDiscount
import com.rfz.appflotal.data.model.promotions.categorizedPromotionEntities
import com.rfz.appflotal.data.model.promotions.toDomain
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
     * Consulta el endpoint una vez por categoría y reemplaza el cache local completo con el
     * resultado. Es la única función que toca la red; la ejecuta el worker diario, no la UI.
     *
     * Una llamada por categoría asegura resultados en cada pestaña ([limit] por categoría).
     * Las llamadas van una tras otra para no cargar al backend, que consulta cada tienda.
     * Si falla alguna, se conserva el cache anterior completo y el worker reintenta.
     */
    suspend fun syncDiscounts(
        limit: Int = 50,
        pagesPerStore: Int = 20
    ): Result<Unit> {
        val token = getTasksUseCase().first().first().fld_token
        val resultsByCategory = PromotionCategory.entries.map { category ->
            val response = remotePromotionsDataSource.getDiscounts(
                token = token,
                category = category.apiValue,
                limit = limit,
                pagesPerStore = pagesPerStore
            ).getOrElse { return Result.failure(it) }
            category to response.result
        }
        localPromotionsDataSource.refreshAll(categorizedPromotionEntities(resultsByCategory))
        return Result.success(Unit)
    }

    /**
     * Lee una página del cache local (fuente de verdad entre sincronizaciones), sin red.
     * [category] null = todas las categorías (búsqueda de la pantalla principal).
     */
    suspend fun getPromotionsPage(
        page: Int,
        pageSize: Int,
        search: String = "",
        store: String = "",
        category: PromotionCategory? = null
    ): Result<PromotionsPage> {
        val offset = (page - 1) * pageSize
        val categoryFilter = category?.apiValue.orEmpty()
        return localPromotionsDataSource.getPage(
            search = search,
            store = store,
            category = categoryFilter,
            limit = pageSize,
            offset = offset
        ).map { entities ->
            val total = localPromotionsDataSource.count(search, store, categoryFilter)
            PromotionsPage(
                items = entities.map { it.toDomain() },
                hasNextPage = offset + entities.size < total
            )
        }
    }

    /**
     * Tiendas presentes en el cache local para [category] (null = todas), para delimitar
     * la búsqueda por tienda.
     */
    suspend fun getStores(category: PromotionCategory? = null): Result<List<String>> =
        localPromotionsDataSource.getStores(category?.apiValue.orEmpty())
}
