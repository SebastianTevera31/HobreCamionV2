package com.rfz.appflotal.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cada migración se valida contra schemas/N.json (runMigrationsAndValidate con
 * validateDroppedTables = true) y comprueba que no se pierdan la sesión ni los datos del usuario.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val testDb = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppHombreCamionDatabase::class.java
    )

    /** Usuario con sesión; el esquema de `user` es igual en las versiones 27 a 32. */
    private fun SupportSQLiteDatabase.insertUser(
        idMonitor: Int = 0,
        monitorMac: String = "",
        baseConfiguration: String = "",
        paymentPlan: String = "Complete"
    ) = execSQL(
        """
        INSERT INTO user (idUser, fld_name, fld_username, fld_email, fld_token, id_monitor,
            country, industry, vehicleType, monitorMac, baseConfiguration, idVehicle,
            vehiclePlates, paymentPlan, fecha, termsGranted, odometer, dateLastOdometer)
        VALUES (7, 'Operador', 'operador', 'op@test.com', 'token-test', $idMonitor,
            1, 1, 'TRUCK', '$monitorMac', '$baseConfiguration', 1, 'ABC123', '$paymentPlan', '', 1, 0, '')
        """.trimIndent()
    )

    /** Accesorio seguido con las columnas de la versión 28. */
    private fun SupportSQLiteDatabase.insertFollowedAccessoryV28(createdAt: Long = 100) = execSQL(
        """
        INSERT INTO followed_accessory_table (catalogAccessoryId, vehicleType, name,
            normalizedName, description, createdAt, initialSyncDone)
        VALUES (1, 'TRUCK', 'Dash cam', 'dash cam', NULL, $createdAt, 1)
        """.trimIndent()
    )

    private fun SupportSQLiteDatabase.insertFollowedPromotion(firstSeenAt: Long) = execSQL(
        """
        INSERT INTO followed_promotion_table (followedAccessoryId, productUrl, position,
            store, title, sku, regularPrice, finalPrice, percentOff, currency, imageUrl,
            firstSeenAt)
        VALUES (1, 'https://store.com/a', 0, 'Store', 'Item', NULL, 100.0, 80.0, 20.0,
            'USD', NULL, $firstSeenAt)
        """.trimIndent()
    )

    private fun SupportSQLiteDatabase.count(table: String): Int =
        query("SELECT COUNT(*) FROM $table").use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

    private fun SupportSQLiteDatabase.storedToken(): String =
        query("SELECT fld_token FROM user").use { cursor ->
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            cursor.getString(0)
        }

    @Test
    fun migrate27To28_existingSession_keepsUserAndCreatesFollowedTables() {
        helper.createDatabase(testDb, 27).apply {
            insertUser(paymentPlan = "FREE")
            close()
        }

        val db = helper.runMigrationsAndValidate(testDb, 28, true, MIGRATION_27_28)

        assertEquals("token-test", db.storedToken())
        db.insertFollowedAccessoryV28()
        assertEquals(1, db.count("followed_accessory_table"))
    }

    @Test
    fun migrate28To29_followedAccessories_keepsRowsWithEmptySyncDates() {
        helper.createDatabase(testDb, 28).apply {
            insertFollowedAccessoryV28()
            insertFollowedPromotion(firstSeenAt = 100)
            close()
        }

        val db = helper.runMigrationsAndValidate(testDb, 29, true, MIGRATION_28_29)

        db.query(
            "SELECT name, initialSyncDone, firstSyncedAt, lastSyncedAt FROM followed_accessory_table"
        ).use { cursor ->
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            assertEquals("Dash cam", cursor.getString(0))
            assertEquals(1, cursor.getInt(1))
            // Las fechas nuevas quedan vacías hasta la siguiente búsqueda.
            assertTrue(cursor.isNull(2))
            assertTrue(cursor.isNull(3))
        }
        assertEquals(1, db.count("followed_promotion_table"))
    }

    @Test
    fun migrate29To30_cachedPromotions_areRecordedAsSeen() {
        helper.createDatabase(testDb, 29).apply {
            execSQL(
                """
                INSERT INTO followed_accessory_table (id, catalogAccessoryId, vehicleType, name,
                    normalizedName, description, createdAt, initialSyncDone, firstSyncedAt,
                    lastSyncedAt)
                VALUES (1, 1, 'TRUCK', 'Dash cam', 'dash cam', NULL, 100, 1, 100, 500)
                """.trimIndent()
            )
            insertFollowedPromotion(firstSeenAt = 200)
            close()
        }

        val db = helper.runMigrationsAndValidate(testDb, 30, true, MIGRATION_29_30)

        db.query(
            "SELECT followedAccessoryId, productUrl, firstSeenAt, lastSeenAt FROM followed_seen_product_table"
        ).use { cursor ->
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            assertEquals(1, cursor.getInt(0))
            assertEquals("https://store.com/a", cursor.getString(1))
            assertEquals(200L, cursor.getLong(2))
            // Última vez visto = última búsqueda del accesorio.
            assertEquals(500L, cursor.getLong(3))
        }
        assertEquals(1, db.count("followed_promotion_table"))
    }

    @Test
    fun migrate30To31_existingSession_keepsUserAndCreatesRemindersTable() {
        helper.createDatabase(testDb, 30).apply {
            insertUser()
            close()
        }

        val db = helper.runMigrationsAndValidate(testDb, 31, true, MIGRATION_30_31)

        assertEquals("token-test", db.storedToken())
        assertEquals(0, db.count("maintenance_notification_table"))
    }

    @Test
    fun migrate31To32_promotionsCache_addsCategoryAndClearsOnlyThatCache() {
        helper.createDatabase(testDb, 31).apply {
            execSQL(
                """
                INSERT INTO promotion_table (productUrl, position, store, title, sku,
                    regularPrice, finalPrice, percentOff, currency, imageUrl)
                VALUES ('https://store.com/a', 0, 'Store', 'Item', NULL, 100.0, 80.0, 20.0,
                    'USD', NULL)
                """.trimIndent()
            )
            insertFollowedAccessoryV28()
            close()
        }

        val db = helper.runMigrationsAndValidate(testDb, 32, true, MIGRATION_31_32)

        // El cache sin categoría se vacía; la app lo vuelve a llenar al sincronizar.
        assertEquals(0, db.count("promotion_table"))
        db.execSQL(
            """
            INSERT INTO promotion_table (productUrl, position, store, title, sku, regularPrice,
                finalPrice, percentOff, currency, imageUrl, category)
            VALUES ('https://store.com/b', 0, 'Store', 'Item', NULL, 100.0, 80.0, 20.0, 'USD',
                NULL, 'Llantas')
            """.trimIndent()
        )
        db.query("SELECT category FROM promotion_table").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Llantas", cursor.getString(0))
        }
        // Lo demás no se toca.
        assertEquals(1, db.count("followed_accessory_table"))
    }

    @Test
    fun migrate32To33_userWithMonitor_marksWizardCompleted() {
        helper.createDatabase(testDb, 32).apply {
            insertUser(idMonitor = 15, monitorMac = "AA:BB", baseConfiguration = "BASE6")
            close()
        }

        val db = helper.runMigrationsAndValidate(testDb, 33, true, MIGRATION_32_33)

        db.query("SELECT fld_token, passWizard, passWizardPending FROM user").use { cursor ->
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            assertEquals("token-test", cursor.getString(0))
            // Ya tenía monitor: no se le vuelve a mostrar el Wizard ni se avisa al servidor.
            assertEquals(1, cursor.getInt(1))
            assertEquals(0, cursor.getInt(2))
        }
    }

    @Test
    fun migrate32To33_userWithoutMonitor_keepsWizardPending() {
        helper.createDatabase(testDb, 32).apply {
            insertUser()
            close()
        }

        val db = helper.runMigrationsAndValidate(testDb, 33, true, MIGRATION_32_33)

        db.query("SELECT passWizard, passWizardPending FROM user").use { cursor ->
            cursor.moveToFirst()
            assertEquals(0, cursor.getInt(0))
            assertEquals(0, cursor.getInt(1))
        }
    }

    @Test
    fun migrate33To34_existingReminders_keepCycleWithoutOdometerBaseline() {
        helper.createDatabase(testDb, 33).apply {
            execSQL(
                """
                INSERT INTO maintenance_notification_table (id, message, daysConf, firstSeenAt,
                    lastNotifiedAt, acknowledgedAt)
                VALUES (2, 'Alinear ruedas', 240, 100, 200, 300)
                """.trimIndent()
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(testDb, 34, true, MIGRATION_33_34)

        db.query(
            "SELECT firstSeenAt, lastNotifiedAt, acknowledgedAt, baselineOdometerKm " +
                "FROM maintenance_notification_table"
        ).use { cursor ->
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            assertEquals(100L, cursor.getLong(0))
            assertEquals(200L, cursor.getLong(1))
            assertEquals(300L, cursor.getLong(2))
            // Sin referencia: se toma del primer odómetro válido en la siguiente evaluación.
            assertTrue(cursor.isNull(3))
        }
    }
}
