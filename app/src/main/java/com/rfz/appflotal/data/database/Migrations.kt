package com.rfz.appflotal.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migraciones explícitas. Sin ellas, fallbackToDestructiveMigration borra toda la base
 * (incluida la sesión del usuario y los registros offline pendientes) al subir la versión.
 * El SQL debe coincidir con schemas/<versión>.json.
 */
val MIGRATION_27_28 = object : Migration(27, 28) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `followed_accessory_table` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`catalogAccessoryId` INTEGER, " +
                "`vehicleType` TEXT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`normalizedName` TEXT NOT NULL, " +
                "`description` TEXT, " +
                "`createdAt` INTEGER NOT NULL, " +
                "`initialSyncDone` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_followed_accessory_table_normalizedName_vehicleType` " +
                "ON `followed_accessory_table` (`normalizedName`, `vehicleType`)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `followed_promotion_table` (" +
                "`followedAccessoryId` INTEGER NOT NULL, " +
                "`productUrl` TEXT NOT NULL, " +
                "`position` INTEGER NOT NULL, " +
                "`store` TEXT NOT NULL, " +
                "`title` TEXT NOT NULL, " +
                "`sku` TEXT, " +
                "`regularPrice` REAL NOT NULL, " +
                "`finalPrice` REAL NOT NULL, " +
                "`percentOff` REAL NOT NULL, " +
                "`currency` TEXT NOT NULL, " +
                "`imageUrl` TEXT, " +
                "`firstSeenAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`followedAccessoryId`, `productUrl`))"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_followed_promotion_table_followedAccessoryId` " +
                "ON `followed_promotion_table` (`followedAccessoryId`)"
        )
    }
}

/** Fechas de primera y última búsqueda por accesorio seguido ("Nuevo" y "Actualizado hace X h"). */
val MIGRATION_28_29 = object : Migration(28, 29) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `followed_accessory_table` ADD COLUMN `firstSyncedAt` INTEGER")
        db.execSQL("ALTER TABLE `followed_accessory_table` ADD COLUMN `lastSyncedAt` INTEGER")
    }
}

/**
 * Productos ya vistos por accesorio, para no volver a notificar uno que sale y regresa a los
 * resultados. Lo que ya está en caché cuenta como visto, para que actualizar la app no lo
 * notifique otra vez.
 */
val MIGRATION_29_30 = object : Migration(29, 30) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `followed_seen_product_table` (" +
                "`followedAccessoryId` INTEGER NOT NULL, " +
                "`productUrl` TEXT NOT NULL, " +
                "`firstSeenAt` INTEGER NOT NULL, " +
                "`lastSeenAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`followedAccessoryId`, `productUrl`))"
        )
        db.execSQL(
            "INSERT OR IGNORE INTO `followed_seen_product_table` " +
                "(`followedAccessoryId`, `productUrl`, `firstSeenAt`, `lastSeenAt`) " +
                "SELECT p.`followedAccessoryId`, p.`productUrl`, p.`firstSeenAt`, " +
                "COALESCE(a.`lastSyncedAt`, p.`firstSeenAt`) " +
                "FROM `followed_promotion_table` p " +
                "JOIN `followed_accessory_table` a ON a.`id` = p.`followedAccessoryId`"
        )
    }
}

/** Catálogo de recordatorios de mantenimiento (api/Catalog/Notifications) y su ciclo de emisión. */
val MIGRATION_30_31 = object : Migration(30, 31) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `maintenance_notification_table` (" +
                "`id` INTEGER NOT NULL, " +
                "`message` TEXT NOT NULL, " +
                "`daysConf` INTEGER NOT NULL, " +
                "`firstSeenAt` INTEGER NOT NULL, " +
                "`lastNotifiedAt` INTEGER, " +
                "`acknowledgedAt` INTEGER, " +
                "PRIMARY KEY(`id`))"
        )
    }
}

/**
 * Categoría de cada promoción (Accesorios, Llantas, Amarre de carga). promotion_table es
 * solo caché: se vacía para no dejar filas sin categoría, y la app la vuelve a llenar al abrir.
 */
val MIGRATION_31_32 = object : Migration(31, 32) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `promotion_table` ADD COLUMN `category` TEXT")
        db.execSQL("DELETE FROM `promotion_table`")
    }
}

val ALL_MIGRATIONS = arrayOf(
    MIGRATION_27_28,
    MIGRATION_28_29,
    MIGRATION_29_30,
    MIGRATION_30_31,
    MIGRATION_31_32
)
