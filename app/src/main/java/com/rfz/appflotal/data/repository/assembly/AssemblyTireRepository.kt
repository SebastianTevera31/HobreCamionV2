package com.rfz.appflotal.data.repository.assembly

import com.rfz.appflotal.data.model.assembly.AssemblyTireDto
import com.rfz.appflotal.data.model.assembly.AssemblyTireMapper.toEntity
import com.rfz.appflotal.data.network.service.assembly.LocalAssemblyDataSource
import com.rfz.appflotal.data.network.service.assembly.RemoteAssemblyDataSource
import retrofit2.Response
import javax.inject.Inject

interface AssemblyTireRepository {
    suspend fun registerAssemblyTire(assemblyTireDto: AssemblyTireDto): Response<Unit>
}

class AssemblyTireRepositoryImpl @Inject constructor(
    private val remoteAssemblyDataSource: RemoteAssemblyDataSource,
    private val localAssemblyDataSource: LocalAssemblyDataSource
) : AssemblyTireRepository {
    override suspend fun registerAssemblyTire(assemblyTireDto: AssemblyTireDto): Response<Unit> {
        val response = remoteAssemblyDataSource.registerAssemblyTire(assemblyTireDto)
        if (response.isSuccessful) {
            localAssemblyDataSource.upsertAssemblyTire(assemblyTireDto.toEntity())
        }
        return response
    }
}