package com.rfz.appflotal.data.repository.axle

import com.rfz.appflotal.data.model.axle.Axle
import com.rfz.appflotal.data.model.axle.toDomain
import com.rfz.appflotal.data.model.axle.toEntity
import com.rfz.appflotal.data.network.service.axle.LocalAxleDataSource
import com.rfz.appflotal.data.network.service.axle.RemoteAxleDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface AxleRepository {
    suspend fun getAxles(): List<Axle>?
}

class AxleRepositoryImpl @Inject constructor(
    private val remoteAxleDataSource: RemoteAxleDataSource,
    private val localAxleDataSource: LocalAxleDataSource,
    private val getTaskUseCase: GetTasksUseCase
) : AxleRepository {

    override suspend fun getAxles(): List<Axle>? {
        try {
            val token = getTaskUseCase().first()[0].fld_token
            val remoteAxleList = remoteAxleDataSource.fetchAxleList(token = token)
            remoteAxleList.onSuccess {
                localAxleDataSource.saveAxles(it.map { value -> value.toEntity() })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val result = localAxleDataSource.getAxle()
        return if (result.isSuccess) result.getOrNull()?.map { value -> value.toDomain() } else null
    }
}