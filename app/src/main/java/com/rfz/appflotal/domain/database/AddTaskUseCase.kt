package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.model.flotalSoft.AppFlotalEntity
import com.rfz.appflotal.data.repository.FscSoftRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(private val appFlotalRepository: FscSoftRepository) {
    suspend operator fun invoke(appFlotalEntity: AppFlotalEntity) {
        appFlotalRepository.addTask(appFlotalEntity)
    }
}