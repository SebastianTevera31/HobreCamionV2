package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rfz.appflotal.data.model.promotions.PromotionEntity

@Dao
interface PromotionsDao {
    @Query(
        """
        SELECT * FROM promotion_table
        WHERE (:search = '' OR title LIKE '%' || :search || '%' OR store LIKE '%' || :search || '%')
        ORDER BY position ASC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getPage(search: String, limit: Int, offset: Int): List<PromotionEntity>

    @Query(
        """
        SELECT COUNT(*) FROM promotion_table
        WHERE (:search = '' OR title LIKE '%' || :search || '%' OR store LIKE '%' || :search || '%')
        """
    )
    suspend fun count(search: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(promotions: List<PromotionEntity>)

    @Query("DELETE FROM promotion_table")
    suspend fun clearAll()

    @Transaction
    suspend fun refreshAll(promotions: List<PromotionEntity>) {
        clearAll()
        insertAll(promotions)
    }
}
