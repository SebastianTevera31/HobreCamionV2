package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.vehicle.UpdateVehicle
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import com.rfz.appflotal.data.repository.vehicle.VehicleRepository
import com.rfz.appflotal.domain.userpreferences.SwitchOdometerUnitUseCase
import com.rfz.appflotal.domain.userpreferences.SwitchPressureUnitUseCase
import com.rfz.appflotal.domain.userpreferences.SwitchTemperatureUnitUseCase
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val appFlotalRepository: HombreCamionRepository,
) {
    suspend operator fun invoke(appFlotalEntity: AppHCEntity) {
        appFlotalRepository.addTask(appFlotalEntity)
    }

    suspend fun updateUserData(
        idUser: Int,
        fldName: String,
        fldEmail: String,
        country: Int,
        industry: Int,
    ) {
        appFlotalRepository.updateUserData(
            idUser = idUser,
            fldName = fldName,
            fldEmail = fldEmail,
            country = country,
            industry = industry,
        )
    }

    suspend fun updateTermsFlag(idUser: Int, flag: Boolean) {
        appFlotalRepository.updateTermsFlag(
            idUser = idUser,
            flag = flag
        )
    }

    suspend fun updateToken(idUser: Int, token: String) {
        appFlotalRepository.updateToken(idUser = idUser, token = token)
    }
}

class UpdateVehicleDataUseCase @Inject constructor(
    private val appFlotalRepository: HombreCamionRepository,
    private val vehicleRepository: VehicleRepository,
    private val switchTemperatureUnitUseCase: SwitchTemperatureUnitUseCase,
    private val switchPressureUnitUseCase: SwitchPressureUnitUseCase,
    private val switchOdometerUnitUseCase: SwitchOdometerUnitUseCase
) {
    suspend operator fun invoke(
        idUser: Int,
        vehicleId: Int,
        vehicleType: String,
        vehiclePlates: String
    ): Result<List<GeneralResponse>> {

        switchTemperatureUnitUseCase()
        switchPressureUnitUseCase()
        switchOdometerUnitUseCase()

        val result = vehicleRepository.updateVehicleData(
            request = UpdateVehicle(
                vehicleId = vehicleId,
                typeVehicle = vehicleType,
                spareTires = 0,
                vehicleNumber = "",
                plates = vehiclePlates,
                dailyMaximumDistance = 0,
                averageDailyDistances = 0
            )
        )

        return result.onSuccess {
            appFlotalRepository.updateVehicleData(
                idUser = idUser,
                vehicleType = vehicleType,
                vehiclePlates = vehiclePlates
            )
        }.onFailure { it.message }
    }
}