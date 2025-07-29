package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.repository.FscSoftRepository
import javax.inject.Inject

class DeleteTasksUseCase @Inject constructor(
    private val repository: FscSoftRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllTasks()
    }
}