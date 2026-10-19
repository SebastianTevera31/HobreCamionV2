package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rfz.appflotal.data.model.promotions.FollowedAccessoryEntity
import com.rfz.appflotal.data.model.promotions.FollowedPromotionEntity
import com.rfz.appflotal.data.model.promotions.FollowedSeenProductEntity
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

    @Query(
        """
        UPDATE followed_accessory_table
        SET initialSyncDone = 1,
            lastSyncedAt = :syncedAt,
            firstSyncedAt = COALESCE(firstSyncedAt, :syncedAt)
        WHERE id = :id
        """
    )
    suspend fun markSynced(id: Long, syncedAt: Long)

    @Query("SELECT * FROM followed_promotion_table ORDER BY followedAccessoryId ASC, percentOff DESC, position ASC")
    fun observeFollowedPromotions(): Flow<List<FollowedPromotionEntity>>

    @Query("SELECT * FROM followed_seen_product_table WHERE followedAccessoryId = :followedAccessoryId")
    suspend fun getSeenProductsFor(followedAccessoryId: Long): List<FollowedSeenProductEntity>

    /** Productos ya vistos por cualquier accesorio seguido, aunque ya no estén en sus resultados. */
    @Query("SELECT DISTINCT productUrl FROM followed_seen_product_table")
    suspend fun getKnownProductUrls(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotions(promotions: List<FollowedPromotionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSeenProducts(seen: List<FollowedSeenProductEntity>)

    @Query("DELETE FROM followed_promotion_table WHERE followedAccessoryId = :followedAccessoryId")
    suspend fun deletePromotionsFor(followedAccessoryId: Long)

    @Query("DELETE FROM followed_seen_product_table WHERE followedAccessoryId = :followedAccessoryId")
    suspend fun deleteSeenProductsFor(followedAccessoryId: Long)

    @Query(
        """
        DELETE FROM followed_seen_product_table
        WHERE followedAccessoryId = :followedAccessoryId AND lastSeenAt < :before
        """
    )
    suspend fun forgetSeenProductsBefore(followedAccessoryId: Long, before: Long)

    @Query("DELETE FROM followed_promotion_table")
    suspend fun clearPromotions()

    @Query("DELETE FROM followed_seen_product_table")
    suspend fun clearSeenProducts()

    @Query("DELETE FROM followed_accessory_table")
    suspend fun clearFollowedAccessories()

    @Transaction
    suspend fun unfollow(id: Long) {
        deletePromotionsFor(id)
        deleteSeenProductsFor(id)
        deleteFollowedAccessory(id)
    }

    /**
     * Guarda el resultado de una búsqueda: reemplaza las promociones del accesorio, registra
     * lo visto y olvida lo que no aparece desde [forgetSeenBefore]. Si el usuario dejó de
     * seguirlo mientras corría la sincronización, no se guarda nada (evita registros huérfanos).
     */
    @Transaction
    suspend fun applySync(
        followedAccessoryId: Long,
        promotions: List<FollowedPromotionEntity>,
        seen: List<FollowedSeenProductEntity>,
        syncedAt: Long,
        forgetSeenBefore: Long
    ) {
        if (countFollowedAccessory(followedAccessoryId) == 0) return
        deletePromotionsFor(followedAccessoryId)
        insertPromotions(promotions)
        upsertSeenProducts(seen)
        forgetSeenProductsBefore(followedAccessoryId, forgetSeenBefore)
        markSynced(followedAccessoryId, syncedAt)
    }

    @Transaction
    suspend fun clearAll() {
        clearPromotions()
        clearSeenProducts()
        clearFollowedAccessories()
    }
}
