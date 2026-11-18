package com.rfz.appflotal.data.repository.assembly

import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.model.assembly.AssemblyTireResponse
import com.rfz.appflotal.data.model.assembly.toDomain
import com.rfz.appflotal.data.model.assembly.toEntity
import com.rfz.appflotal.data.network.service.assembly.LocalAssemblyDataSource
import com.rfz.appflotal.data.network.service.assembly.RemoteAssemblyDataSource
import com.rfz.appflotal.data.network.service.sincronizacion.LocalOperacionesLlantaDataSource
import com.rfz.appflotal.domain.database.GetTasksUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AssemblyTireRepository {
    suspend fun observeAssemblyTire(): Flow<List<AssemblyTire>>
    suspend fun getAssemblyTire(positionTire: String): AssemblyTire?
    suspend fun confirmTireMounted(positionTire: String): Boolean
    suspend fun refreshMountedTires(): Result<List<AssemblyTireResponse>>
}

class AssemblyTireRepositoryImpl @Inject constructor(
    private val localAssemblyDataSource: LocalAssemblyDataSource,
    private val remoteAssemblyDataSource: RemoteAssemblyDataSource,
    private val localOperaciones: LocalOperacionesLlantaDataSource,
    private val getTasksUseCase: GetTasksUseCase,
) : AssemblyTireRepository {
    override suspend fun observeAssemblyTire(): Flow<List<AssemblyTire>> =
        localAssemblyDataSource.observeAssemblyTire().map { list -> list.map { it.toDomain() } }

    override suspend fun getAssemblyTire(positionTire: String): AssemblyTire? {
        val result = localAssemblyDataSource.getAssemblyTire(positionTire)
        return if (result.isSuccess) result.getOrNull()?.toDomain() else null
    }

    override suspend fun confirmTireMounted(positionTire: String): Boolean {
        val result = localAssemblyDataSource.getAssemblyTire(positionTire)
        return if (result.isSuccess) result.getOrNull()?.positionTire != null else false
    }

    /** Reemplaza los montajes locales por los del servidor, salvo las posiciones con envíos en cola. */
    override suspend fun refreshMountedTires(): Result<List<AssemblyTireResponse>> {
        val userData = getTasksUseCase().first()[0]
        return remoteAssemblyDataSource.fetchMountedTire(userData.fld_token, userData.id_monitor)
            .onSuccess { montados ->
                localOperaciones.reconciliarMontajes(
                    delServidor = montados.map { it.toEntity().copy(idMonitor = userData.id_monitor) },
                    idUsuario = userData.idUser
                )
            }
    }
}
