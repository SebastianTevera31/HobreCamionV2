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

val ALL_MIGRATIONS = arrayOf(MIGRATION_27_28)
