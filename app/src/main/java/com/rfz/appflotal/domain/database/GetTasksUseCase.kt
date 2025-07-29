package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.model.flotalSoft.AppFlotalEntity
import com.rfz.appflotal.data.repository.FscSoftRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: FscSoftRepository
) {
    suspend operator fun invoke(): Flow<List<AppFlotalEntity>> {
        return repository.getTasks()
    }
}

