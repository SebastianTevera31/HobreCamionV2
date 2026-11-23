package com.rfz.appflotal.data.database

import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.RoomDatabase
import com.rfz.appflotal.data.dao.AppHCDao
import com.rfz.appflotal.data.dao.AssemblyTireDao
import com.rfz.appflotal.data.dao.AxleDao
import com.rfz.appflotal.data.dao.CoordinatesDao
import com.rfz.appflotal.data.dao.DataframeDao
import com.rfz.appflotal.data.dao.OperacionPendienteDao
import com.rfz.appflotal.data.dao.FollowedPromotionsDao
import com.rfz.appflotal.data.dao.ForumDao
import com.rfz.appflotal.data.dao.CatalogoDao
import com.rfz.appflotal.data.dao.MaintenanceNotificationsDao
import com.rfz.appflotal.data.dao.PromotionsDao
import com.rfz.appflotal.data.dao.SensorDataDao
import com.rfz.appflotal.data.model.assembly.AssemblyTireEntity
import com.rfz.appflotal.data.model.axle.AxleEntity
import com.rfz.appflotal.data.model.catalog.MaintenanceNotificationEntity
import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.model.database.CommentEntity
import com.rfz.appflotal.data.model.database.CoordinatesEntity
import com.rfz.appflotal.data.model.database.DataframeEntity
import com.rfz.appflotal.data.model.catalogo.CatalogoEntity
import com.rfz.appflotal.data.model.catalogo.DescargaCatalogoEntity
import com.rfz.appflotal.data.model.catalogo.LlantaEntity
import com.rfz.appflotal.data.model.database.SensorDataEntity
import com.rfz.appflotal.data.model.database.TopicEntity
import com.rfz.appflotal.data.model.promotions.FollowedAccessoryEntity
import com.rfz.appflotal.data.model.promotions.FollowedPromotionEntity
import com.rfz.appflotal.data.model.promotions.FollowedSeenProductEntity
import com.rfz.appflotal.data.model.promotions.PromotionEntity
import com.rfz.appflotal.data.model.sincronizacion.OperacionPendienteEntity

@Keep
@Database(
    entities = [
        AppHCEntity::class, DataframeEntity::class, CoordinatesEntity::class,
        SensorDataEntity::class, AssemblyTireEntity::class, AxleEntity::class,
        OperacionPendienteEntity::class,
        CatalogoEntity::class, LlantaEntity::class, DescargaCatalogoEntity::class,
        CommentEntity::class, TopicEntity::class,
        PromotionEntity::class, FollowedAccessoryEntity::class, FollowedPromotionEntity::class,
        FollowedSeenProductEntity::class, MaintenanceNotificationEntity::class
    ],
    version = 36,
    exportSchema = true
)
abstract class AppHombreCamionDatabase : RoomDatabase() {
    //DAO
    abstract fun hcSoftDao(): AppHCDao
    abstract fun dataframeDao(): DataframeDao
    abstract fun coordinatesDao(): CoordinatesDao
    abstract fun sensorDataDao(): SensorDataDao

    abstract fun assemblyTireDao(): AssemblyTireDao
    abstract fun operacionPendienteDao(): OperacionPendienteDao
    abstract fun catalogoDao(): CatalogoDao

    abstract fun axleDao(): AxleDao

    abstract fun forumDao(): ForumDao

    abstract fun promotionsDao(): PromotionsDao

    abstract fun followedPromotionsDao(): FollowedPromotionsDao

    abstract fun maintenanceNotificationsDao(): MaintenanceNotificationsDao
}