package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.model.flotalSoft.AppHCEntity
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(private val appFlotalRepository: HombreCamionRepository) {
    suspend operator fun invoke(appFlotalEntity: AppHCEntity) {
        appFlotalRepository.addTask(appFlotalEntity)
    }
}