package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.model.promotions.StoreDiscount
import com.rfz.appflotal.data.network.service.promotions.RemotePromotionsDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PromotionsRepository @Inject constructor(
    private val remotePromotionsDataSource: RemotePromotionsDataSource,
    private val getTasksUseCase: GetTasksUseCase
) {

    suspend fun getDiscounts(
        search: String = "",
        store: String = "",
        limit: Int = 50,
        pagesPerStore: Int = 20
    ): Result<List<StoreDiscount>> {
        val token = getTasksUseCase().first().first().fld_token
        return remotePromotionsDataSource.getDiscounts(
            token = token,
            search = search,
            store = store,
            limit = limit,
            pagesPerStore = pagesPerStore
        ).map { response -> response.result }
    }
}
