package com.rfz.appflotal.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val testDb = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppHombreCamionDatabase::class.java
    )

    @Test
    fun migrate27To28_conservaLaSesionYCreaLasTablasDeSeguidos() {
        helper.createDatabase(testDb, 27).apply {
            execSQL(
                """
                INSERT INTO user (idUser, fld_name, fld_username, fld_email, fld_token, id_monitor,
                    country, industry, vehicleType, monitorMac, baseConfiguration, idVehicle,
                    vehiclePlates, paymentPlan, fecha, termsGranted, odometer, dateLastOdometer)
                VALUES (7, 'Operador', 'operador', 'op@test.com', 'token-test', 0,
                    1, 1, 'TRUCK', '', '', 1, 'ABC123', 'FREE', '', 1, 0, '')
                """.trimIndent()
            )
            close()
        }

        // validateDroppedTables = true y la validación del esquema fallan si el SQL de la
        // migración no coincide con schemas/28.json.
        val db = helper.runMigrationsAndValidate(testDb, 28, true, MIGRATION_27_28)

        db.query("SELECT idUser, fld_token FROM user").use { cursor ->
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            assertEquals(7, cursor.getInt(0))
            assertEquals("token-test", cursor.getString(1))
        }

        db.execSQL(
            """
            INSERT INTO followed_accessory_table (catalogAccessoryId, vehicleType, name,
                normalizedName, description, createdAt, initialSyncDone)
            VALUES (1, 'TRUCK', 'Dash cam', 'dash cam', NULL, 0, 0)
            """.trimIndent()
        )
        db.query("SELECT COUNT(*) FROM followed_accessory_table").use { cursor ->
            cursor.moveToFirst()
            assertEquals(1, cursor.getInt(0))
        }
    }

    @Test
    fun migrate28To29_conservaLosSeguidosYAgregaLasFechasDeBusqueda() {
        helper.createDatabase(testDb, 28).apply {
            execSQL(
                """
                INSERT INTO followed_accessory_table (catalogAccessoryId, vehicleType, name,
                    normalizedName, description, createdAt, initialSyncDone)
                VALUES (1, 'TRUCK', 'Dash cam', 'dash cam', NULL, 100, 1)
                """.trimIndent()
            )
            execSQL(
                """
                INSERT INTO followed_promotion_table (followedAccessoryId, productUrl, position,
                    store, title, sku, regularPrice, finalPrice, percentOff, currency, imageUrl,
                    firstSeenAt)
                VALUES (1, 'https://store.com/a', 0, 'Store', 'Item', NULL, 100.0, 80.0, 20.0,
                    'USD', NULL, 100)
                """.trimIndent()
            )
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
            assertEquals(true, cursor.isNull(2))
            assertEquals(true, cursor.isNull(3))
        }
        db.query("SELECT COUNT(*) FROM followed_promotion_table").use { cursor ->
            cursor.moveToFirst()
            assertEquals(1, cursor.getInt(0))
        }
    }
}
