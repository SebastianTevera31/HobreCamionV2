package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.model.database.AppHCEntity
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(private val appFlotalRepository: HombreCamionRepository) {
    suspend operator fun invoke(appFlotalEntity: AppHCEntity) {
        appFlotalRepository.addTask(appFlotalEntity)
    }

    suspend fun updateUserData(
        idUser: Int,
        fldName: String,
        fldEmail: String,
        country: Int,
        industry: Int,
        vehiclePlates: String,
        vehicleType: String
    ) {
        appFlotalRepository.updateUserData(
            idUser = idUser,
            fldName = fldName,
            fldEmail = fldEmail,
            vehiclePlates = vehiclePlates,
            country = country,
            industry = industry,
            vehicleType = vehicleType
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