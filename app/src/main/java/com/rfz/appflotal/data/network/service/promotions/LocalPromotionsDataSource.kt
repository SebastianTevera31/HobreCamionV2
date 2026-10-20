package com.rfz.appflotal.data.network.service.promotions

import android.database.sqlite.SQLiteException
import com.rfz.appflotal.data.dao.PromotionsDao
import com.rfz.appflotal.data.model.promotions.PromotionEntity
import com.rfz.appflotal.data.network.service.DataError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalPromotionsDataSource @Inject constructor(
    private val promotionsDao: PromotionsDao
) {

    suspend fun getPage(
        search: String,
        store: String,
        category: String,
        limit: Int,
        offset: Int
    ): Result<List<PromotionEntity>> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(
                    promotionsDao.getPage(
                        search = search,
                        store = store,
                        category = category,
                        limit = limit,
                        offset = offset
                    )
                )
            } catch (e: SQLiteException) {
                Result.failure(DataError.Local(e))
            } catch (_: Exception) {
                Result.failure(DataError.Unknown())
            }
        }

    suspend fun count(search: String, store: String, category: String): Int =
        withContext(Dispatchers.IO) {
            promotionsDao.count(search, store, category)
        }

    suspend fun getStores(category: String): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            Result.success(promotionsDao.getStores(category))
        } catch (e: SQLiteException) {
            Result.failure(DataError.Local(e))
        } catch (_: Exception) {
            Result.failure(DataError.Unknown())
        }
    }

    suspend fun refreshAll(promotions: List<PromotionEntity>) = withContext(Dispatchers.IO) {
        promotionsDao.refreshAll(promotions)
    }
}
