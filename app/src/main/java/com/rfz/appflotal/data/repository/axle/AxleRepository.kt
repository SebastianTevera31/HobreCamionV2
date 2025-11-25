package com.rfz.appflotal.data.repository.axle

import com.rfz.appflotal.data.model.axle.Axle
import com.rfz.appflotal.data.model.axle.toDomain
import com.rfz.appflotal.data.model.axle.toEntity
import com.rfz.appflotal.data.network.service.axle.LocalAxleDataSource
import com.rfz.appflotal.data.network.service.axle.RemoteAxleDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AxleRepository {
    suspend fun getAxles(): List<Axle>
}

class AxleRepositoryImpl @Inject constructor(
    private val remoteAxleDataSource: RemoteAxleDataSource,
    private val localAxleDataSource: LocalAxleDataSource,
    private val getTaskUseCase: GetTasksUseCase
) : AxleRepository {

    override suspend fun getAxles(): List<Axle> {
        try {
            val token = getTaskUseCase().first()[0].fld_token
            val remoteAxleList = remoteAxleDataSource.fetchAxleList(token = token)
            localAxleDataSource.saveAxles(remoteAxleList.map { value -> value.toEntity() })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return localAxleDataSource.getAxle().map { value -> value.toDomain() }
    }
}