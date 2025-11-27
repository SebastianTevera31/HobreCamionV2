package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rfz.appflotal.data.model.database.AppHCEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface AppHCDao {
    @Query("SELECT * from user")
    fun getData(): Flow<List<AppHCEntity>>

    @Query("UPDATE user SET id_monitor =:idMonitor, monitorMac =:mac, baseConfiguration =:baseConfiguration WHERE idUser =:idUser")
    suspend fun updateMonitorId(idMonitor: Int, mac: String, baseConfiguration: String, idUser: Int)

    @Query(
        "UPDATE user SET fld_name =:fldName, fld_email =:fldEmail, " +
                "country =:country, industry =:industry, vehiclePlates =:vehiclePlates, " +
                "vehicleType =:vehicleType  WHERE idUser =:idUser"
    )
    suspend fun updateUserData(
        idUser: Int,
        fldName: String,
        fldEmail: String,
        industry: Int,
        country: Int,
        vehiclePlates: String,
        vehicleType: String,
    )

    @Query("UPDATE user SET termsGranted =:flag WHERE idUser =:idUser")
    suspend fun updateTermsFlag(idUser: Int, flag: Boolean)

    @Query("UPDATE user SET fld_token =:token WHERE idUser =:idUser")
    suspend fun updateToken(idUser: Int, token: String)

    @Query("UPDATE user SET odometer =:odometer")
    suspend fun updateOdometer(odometer: Int)

    @Query("SELECT odometer FROM user")
    suspend fun getOdometer(): Int

    @Query("DELETE FROM user")
    suspend fun deleteAllFlotalSoft()

    @Insert
    suspend fun addFlotalSoft(item: AppHCEntity)
}