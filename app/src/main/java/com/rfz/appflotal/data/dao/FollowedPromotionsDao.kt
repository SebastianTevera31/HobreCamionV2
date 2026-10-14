package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rfz.appflotal.data.model.promotions.FollowedAccessoryEntity
import com.rfz.appflotal.data.model.promotions.FollowedPromotionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowedPromotionsDao {

    @Query("SELECT * FROM followed_accessory_table ORDER BY createdAt ASC")
    fun observeFollowedAccessories(): Flow<List<FollowedAccessoryEntity>>

    @Query("SELECT * FROM followed_accessory_table ORDER BY createdAt ASC")
    suspend fun getFollowedAccessories(): List<FollowedAccessoryEntity>

    @Query("SELECT COUNT(*) FROM followed_accessory_table WHERE id = :id")
    suspend fun countFollowedAccessory(id: Long): Int

    /** Devuelve -1 si ya existe el mismo nombre normalizado para el mismo tipo de vehículo. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFollowedAccessory(accessory: FollowedAccessoryEntity): Long

    @Query("DELETE FROM followed_accessory_table WHERE id = :id")
    suspend fun deleteFollowedAccessory(id: Long)

    @Query("UPDATE followed_accessory_table SET initialSyncDone = 1 WHERE id = :id")
    suspend fun markInitialSyncDone(id: Long)

    @Query("SELECT * FROM followed_promotion_table ORDER BY followedAccessoryId ASC, position ASC")
    fun observeFollowedPromotions(): Flow<List<FollowedPromotionEntity>>

    @Query("SELECT * FROM followed_promotion_table WHERE followedAccessoryId = :followedAccessoryId")
    suspend fun getPromotionsFor(followedAccessoryId: Long): List<FollowedPromotionEntity>

    @Query("SELECT DISTINCT productUrl FROM followed_promotion_table")
    suspend fun getKnownProductUrls(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotions(promotions: List<FollowedPromotionEntity>)

    @Query("DELETE FROM followed_promotion_table WHERE followedAccessoryId = :followedAccessoryId")
    suspend fun deletePromotionsFor(followedAccessoryId: Long)

    @Query("DELETE FROM followed_promotion_table")
    suspend fun clearPromotions()

    @Query("DELETE FROM followed_accessory_table")
    suspend fun clearFollowedAccessories()

    @Transaction
    suspend fun unfollow(id: Long) {
        deletePromotionsFor(id)
        deleteFollowedAccessory(id)
    }

    /**
     * Reemplaza las promociones de un accesorio. Si el usuario dejó de seguirlo mientras
     * corría la sincronización, no se insertan (evita registros huérfanos).
     */
    @Transaction
    suspend fun replacePromotionsFor(
        followedAccessoryId: Long,
        promotions: List<FollowedPromotionEntity>
    ) {
        if (countFollowedAccessory(followedAccessoryId) == 0) return
        deletePromotionsFor(followedAccessoryId)
        insertPromotions(promotions)
        markInitialSyncDone(followedAccessoryId)
    }

    @Transaction
    suspend fun clearAll() {
        clearPromotions()
        clearFollowedAccessories()
    }
}
