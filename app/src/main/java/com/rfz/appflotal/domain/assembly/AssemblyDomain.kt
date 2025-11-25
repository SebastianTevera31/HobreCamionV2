package com.rfz.appflotal.domain.assembly

import com.rfz.appflotal.data.model.assembly.AssemblyTire
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import javax.inject.Inject


class AddAssemblyTire @Inject constructor(private val assemblyTireRepository: AssemblyTireRepository) {
    suspend operator fun invoke(assemblyTire: AssemblyTire): Result<Unit> =
        runCatching { assemblyTireRepository.addAssemblyTire(assemblyTire) }
}