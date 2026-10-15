package com.rfz.appflotal.data.repository.promotions

import com.rfz.appflotal.data.dao.FollowedPromotionsDao
import com.rfz.appflotal.data.model.catalog.TopTenAccessoryResponse
import com.rfz.appflotal.data.model.promotions.AccessoryVehicleType
import com.rfz.appflotal.data.model.promotions.FollowedAccessory
import com.rfz.appflotal.data.model.promotions.FollowedAccessoryEntity
import com.rfz.appflotal.data.model.promotions.FollowedPromotion
import com.rfz.appflotal.data.model.promotions.NewFollowedPromotion
import com.rfz.appflotal.data.model.promotions.toDomain
import com.rfz.appflotal.data.model.promotions.toFollowedEntity
import com.rfz.appflotal.data.network.service.DataError
import com.rfz.appflotal.data.network.service.catalog.RemoteCatalogDataSource
import com.rfz.appflotal.data.network.service.promotions.RemotePromotionsDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject

/** Llamadas simultáneas a GetDiscounts durante una sincronización. */
private const val SYNC_CONCURRENCY = 3

class FollowedPromotionsRepository @Inject constructor(
    private val remoteCatalogDataSource: RemoteCatalogDataSource,
    private val remotePromotionsDataSource: RemotePromotionsDataSource,
    private val followedPromotionsDao: FollowedPromotionsDao,
    private val getTasksUseCase: GetTasksUseCase
) {

    suspend fun getSuggestedAccessories(): Result<List<TopTenAccessoryResponse>> =
        remoteCatalogDataSource.getTopTenAccessories()

    suspend fun getUserVehicleType(): AccessoryVehicleType =
        vehicleTypeForUser(getTasksUseCase().first().firstOrNull()?.vehicleType)

    fun observeFollowedAccessories(): Flow<List<FollowedAccessory>> = combine(
        followedPromotionsDao.observeFollowedAccessories(),
        followedPromotionsDao.observeFollowedPromotions()
    ) { accessories, promotions ->
        val now = System.currentTimeMillis()
        val promotionsByAccessory = promotions.groupBy { it.followedAccessoryId }
        accessories.mapNotNull { accessory ->
            accessory.toDomain(
                promotionsByAccessory[accessory.id].orEmpty().map { promotion ->
                    FollowedPromotion(
                        discount = promotion.toDomain(),
                        isNew = isNewPromotion(
                            firstSeenAt = promotion.firstSeenAt,
                            accessoryFirstSyncedAt = accessory.firstSyncedAt,
                            now = now
                        )
                    )
                }
            )
        }
    }

    fun observeFollowedAccessory(id: Long): Flow<FollowedAccessory?> =
        observeFollowedAccessories().map { accessories -> accessories.find { it.id == id } }

    suspend fun follow(
        rawName: String,
        vehicleType: AccessoryVehicleType,
        catalogAccessoryId: Int? = null,
        description: String? = null
    ): FollowValidation {
        val validation = validateNewFollow(
            rawName = rawName,
            vehicleType = vehicleType,
            followed = followedPromotionsDao.getFollowedAccessories()
        )
        if (validation != FollowValidation.VALID) return validation

        val insertedId = followedPromotionsDao.insertFollowedAccessory(
            FollowedAccessoryEntity(
                catalogAccessoryId = catalogAccessoryId,
                vehicleType = vehicleType.name,
                name = cleanAccessoryName(rawName),
                normalizedName = normalizeAccessoryName(rawName),
                description = description?.takeIf { it.isNotBlank() },
                createdAt = System.currentTimeMillis()
            )
        )
        // -1: el índice único detectó un duplicado insertado en paralelo.
        return if (insertedId == -1L) FollowValidation.DUPLICATE else FollowValidation.VALID
    }

    suspend fun unfollow(followedAccessoryId: Long) {
        followedPromotionsDao.unfollow(followedAccessoryId)
    }

    /** Al cerrar sesión: los seguidos son por usuario y solo viven en este teléfono. */
    suspend fun clearAll() {
        followedPromotionsDao.clearAll()
    }

    /**
     * Consulta GetDiscounts por cada accesorio seguido y actualiza la caché.
     * Devuelve las promociones nuevas que se deben notificar.
     *
     * Falla solo si no se pudo consultar ningún accesorio; si fallan algunos, conservan
     * su caché anterior y se reintentan en la siguiente ejecución.
     */
    suspend fun syncFollowedPromotions(): Result<List<NewFollowedPromotion>> {
        val token = getTasksUseCase().first().firstOrNull()?.fld_token
            ?.takeIf { it.isNotBlank() }
            ?: return Result.success(emptyList())

        val followed = followedPromotionsDao.getFollowedAccessories()
        if (followed.isEmpty()) return Result.success(emptyList())

        val semaphore = Semaphore(SYNC_CONCURRENCY)
        val responses = coroutineScope {
            followed.map { accessory ->
                async {
                    accessory to semaphore.withPermit {
                        remotePromotionsDataSource.getDiscounts(
                            token = token,
                            search = accessory.name,
                            limit = RESULTS_PER_FOLLOWED_ACCESSORY
                        )
                    }
                }
            }.awaitAll()
        }

        val knownProductUrls = followedPromotionsDao.getKnownProductUrls().toMutableSet()
        val toNotify = mutableListOf<NewFollowedPromotion>()
        val now = System.currentTimeMillis()

        responses.forEach { (accessory, result) ->
            val fetched = result.getOrNull()?.result ?: return@forEach
            val previousFirstSeen = followedPromotionsDao.getPromotionsFor(accessory.id)
                .associate { it.productUrl to it.firstSeenAt }

            val newOnes = newPromotionsToNotify(accessory, fetched, knownProductUrls)
            toNotify += newOnes

            followedPromotionsDao.replacePromotionsFor(
                followedAccessoryId = accessory.id,
                promotions = fetched.distinctBy { it.productUrl }.mapIndexed { index, discount ->
                    discount.toFollowedEntity(
                        followedAccessoryId = accessory.id,
                        position = index,
                        firstSeenAt = previousFirstSeen[discount.productUrl] ?: now
                    )
                },
                // Mismo instante que firstSeenAt: lo de la primera búsqueda no cuenta como "Nuevo".
                syncedAt = now
            )
            // Una misma promoción encontrada por dos accesorios se notifica una sola vez.
            knownProductUrls += fetched.map { it.productUrl }
        }

        val failures = responses.mapNotNull { (_, result) -> result.exceptionOrNull() }
        return if (failures.size == responses.size) {
            Result.failure(failures.first())
        } else {
            Result.success(toNotify)
        }
    }
}

/** 401/403: la sesión ya no es válida; reintentar no sirve hasta que el usuario vuelva a entrar. */
fun Throwable.isAuthError(): Boolean = this is DataError.Http && (code == 401 || code == 403)
