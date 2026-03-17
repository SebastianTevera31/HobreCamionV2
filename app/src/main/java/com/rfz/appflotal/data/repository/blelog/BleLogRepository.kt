package com.rfz.appflotal.data.repository.blelog

import com.rfz.appflotal.data.network.service.blelog.RemoteBleLogDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BleLogRepository @Inject constructor(
    private val remoteBleLogDataSource: RemoteBleLogDataSource,
    private val getTasksUseCase: GetTasksUseCase
) {

    suspend fun sendDataFrame(dataFrame: String) {
        val user = getTasksUseCase().first().first()
//        remoteBleLogDataSource.sendDataFrame(
//            token = TODO(),
//            data = TODO()
//        )

    }

    suspend fun sendBleError(error: String) {
        val user = getTasksUseCase().first().first()
    }

}