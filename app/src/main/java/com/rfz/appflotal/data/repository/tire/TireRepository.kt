package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.tire.RepairedTire
import com.rfz.appflotal.data.model.tire.RetreatedTire
import com.rfz.appflotal.data.model.tire.toDto
import com.rfz.appflotal.data.network.service.tire.RemoteTireDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface TireRepository {
    suspend fun postRetreatedTire(retreatedTire: RetreatedTire)
    suspend fun postRepairedTire(repairedTire: RepairedTire)
}

class TireRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteTireDataSource,
    private val getTasksUseCase: GetTasksUseCase
) :
    TireRepository {
    override suspend fun postRetreatedTire(retreatedTire: RetreatedTire) {
        val token = getTasksUseCase().first().first().fld_token
        remoteDataSource.postRetreatedTire(token, retreatedTire.toDto())
    }

    override suspend fun postRepairedTire(repairedTire: RepairedTire) {
        val token = getTasksUseCase().first().first().fld_token
        remoteDataSource.postRepairedTire(token, repairedTire.toDto())
    }
}