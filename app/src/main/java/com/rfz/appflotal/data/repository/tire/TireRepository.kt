package com.rfz.appflotal.data.repository.tire

import com.rfz.appflotal.data.model.message.response.GeneralResponse
import com.rfz.appflotal.data.model.tire.ChangeDestination
import com.rfz.appflotal.data.model.tire.RepairedTire
import com.rfz.appflotal.data.model.tire.RetreatedTire
import com.rfz.appflotal.data.model.tire.toDto
import com.rfz.appflotal.data.network.service.tire.RemoteTireDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface TireRepository {
    suspend fun postRetreatedTire(retreatedTire: RetreatedTire): Result<List<GeneralResponse>>
    suspend fun postRepairedTire(repairedTire: RepairedTire): Result<List<GeneralResponse>>

    suspend fun saveDestinationChange(changeDestination: ChangeDestination): Result<List<GeneralResponse>>
}

class TireRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteTireDataSource,
    private val getTasksUseCase: GetTasksUseCase
) :
    TireRepository {
    override suspend fun postRetreatedTire(retreatedTire: RetreatedTire): Result<List<GeneralResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteDataSource.postRetreatedTire(token, retreatedTire.toDto())
    }

    override suspend fun postRepairedTire(repairedTire: RepairedTire): Result<List<GeneralResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteDataSource.postRepairedTire(token, repairedTire.toDto())
    }

    override suspend fun saveDestinationChange(changeDestination: ChangeDestination): Result<List<GeneralResponse>> {
        val token = getTasksUseCase().first().first().fld_token
        return remoteDataSource.postChangeDestination(token, changeDestination.toDto())
    }
}