package com.rfz.appflotal.domain.database

import com.rfz.appflotal.data.model.flotalSoft.AppHCEntity
import com.rfz.appflotal.data.repository.database.HombreCamionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: HombreCamionRepository
) {
    suspend operator fun invoke(): Flow<List<AppHCEntity>> {
        return repository.getTasks()
    }
}

