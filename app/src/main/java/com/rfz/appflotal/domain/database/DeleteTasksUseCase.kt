package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import javax.inject.Inject

class DeleteTasksUseCase @Inject constructor(
    private val repository: HombreCamionRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllTasks()
    }
}