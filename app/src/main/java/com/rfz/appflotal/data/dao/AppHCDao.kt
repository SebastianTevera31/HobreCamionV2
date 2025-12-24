package com.rfz.appflotal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rfz.appflotal.data.model.OdometerData
import com.rfz.appflotal.data.model.database.AppHCEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface AppHCDao {
    @Query("SELECT paymentPlan from user WHERE idUser =:userId")
    fun getPaymentPlan(userId: Int): Flow<String?>

    @Query("SELECT * from user")
    fun getData(): Flow<List<AppHCEntity>>

    @Query("UPDATE user SET id_monitor =:idMonitor, monitorMac =:mac, baseConfiguration =:baseConfiguration WHERE idUser =:idUser")
    suspend fun updateMonitorId(idMonitor: Int, mac: String, baseConfiguration: String, idUser: Int)

    @Query(
        "UPDATE user SET fld_name =:fldName, fld_email =:fldEmail, " +
                "country =:country, industry =:industry WHERE idUser =:idUser"
    )
    suspend fun updateUserData(
        idUser: Int,
        fldName: String,
        fldEmail: String,
        industry: Int,
        country: Int,
    )

    @Query("UPDATE user SET vehicleType =:vehicleType, vehiclePlates =:vehiclePlates WHERE idUser =:idUser")
    suspend fun updateVehicleData(vehicleType: String, vehiclePlates: String, idUser: Int)

    @Query("UPDATE user SET termsGranted =:flag WHERE idUser =:idUser")
    suspend fun updateTermsFlag(idUser: Int, flag: Boolean)

    @Query("UPDATE user SET fld_token =:token WHERE idUser =:idUser")
    suspend fun updateToken(idUser: Int, token: String)

    @Query("UPDATE user SET odometer =:odometer, dateLastOdometer =:date")
    suspend fun updateOdometer(odometer: Int, date: String)

    @Query("SELECT odometer, dateLastOdometer FROM user")
    suspend fun getOdometer(): OdometerData?

    @Query("DELETE FROM user")
    suspend fun deleteAllFlotalSoft()

    @Insert
    suspend fun addFlotalSoft(item: AppHCEntity)

    @Query("UPDATE user SET paymentPlan =:plan WHERE idUser =:idUser")
    suspend fun updateUserPlan(idUser: Int, plan: String)
}